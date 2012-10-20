/**
 * 
 */
package com.djt.social.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.djt.social.model.SocialConnectionData;
import com.djt.social.persistence.ISocialConnectionRepositoryFactoryLocator;

/**
 * 
 * @author Tom.Myers
 * <pre>
	CREATE TABLE SOCIAL_USER_CONNECTION (
	    PROMO_DEPLOY_PATH varchar(128) not null,
	    PROVIDER_ID varchar(128) not null,
	    UID varchar(128) not null,
	    ACCESS_TOKEN varchar(255) not null,
	    ACCESS_TOKEN_SECRET varchar(255),
	    primary key (PROMO_DEPLOY_PATH, PROVIDER_ID, UID));
 * </pre>
 */
public final class SocialConnectionRepositoryImpl implements ConnectionRepository {

	/* */
	private static final String INSERT_USER_CONNECTION_SQL = "INSERT INTO SOCIAL_USER_CONNECTION (PROMO_DEPLOY_PATH, PROVIDER_ID, UID, ACCESS_TOKEN, ACCESS_TOKEN_SECRET) VALUES (?, ?, ?, ?, ?)";

	/* */
	private static final String UPDATE_USER_CONNECTION_SQL = "UPDATE SOCIAL_USER_CONNECTION SET ACCESS_TOKEN = ?, ACCESS_TOKEN_SECRET = ? WHERE PROMO_DEPLOY_PATH = ? AND PROVIDER_ID = ? AND UID = ?";
		
	/* */
	private static final String SELECT_ALL_USER_CONNECTIONS_SQL = "SELECT PROMO_DEPLOY_PATH, PROVIDER_ID, UID, ACCESS_TOKEN, ACCESS_TOKEN_SECRET FROM SOCIAL_USER_CONNECTION WHERE PROMO_DEPLOY_PATH = ? AND UID = ? ORDER BY PROVIDER_ID";
	
	/* */
	private static final String SELECT_USER_CONNECTION_SQL = "SELECT PROMO_DEPLOY_PATH, PROVIDER_ID, UID, ACCESS_TOKEN, ACCESS_TOKEN_SECRET FROM SOCIAL_USER_CONNECTION WHERE PROMO_DEPLOY_PATH = ? AND PROVIDER_ID = ? AND UID = ?";
	
	/* */
	private static final String DELETE_USER_CONNECTION_SQL = "DELETE FROM SOCIAL_USER_CONNECTION WHERE PROMO_DEPLOY_PATH = ? AND PROVIDER_ID = ? and UID = ?";
	
	
	/* */
	private final String promotionDeployPath;
	
	/* */
	private final String uid;
	
	/* */
	private final JdbcTemplate jdbcTemplate;
	
	/* */
	private final ISocialConnectionRepositoryFactoryLocator socialConnectionRepositoryFactoryLocator;

	/* */
	private final TextEncryptor textEncryptor;

	/* */
	private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();
	
	/**
	 * @param promotionDeployPath
	 * @param uid
	 * @param jdbcTemplate
	 * @param socialConnectionRepositoryFactoryLocator
	 * @param textEncryptor
	 */
	public SocialConnectionRepositoryImpl(
		String promotionDeployPath,
		String uid, 
		JdbcTemplate jdbcTemplate, 
		ISocialConnectionRepositoryFactoryLocator socialConnectionRepositoryFactoryLocator, 
		TextEncryptor textEncryptor) {
	
		this.promotionDeployPath = promotionDeployPath;
		this.uid = uid;
		this.jdbcTemplate = jdbcTemplate;
		this.socialConnectionRepositoryFactoryLocator = socialConnectionRepositoryFactoryLocator;
		this.textEncryptor = textEncryptor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#findAllConnections()
	 */
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		
		List<Connection<?>> resultList = jdbcTemplate.query(
			SELECT_ALL_USER_CONNECTIONS_SQL, 
			connectionMapper, 
			promotionDeployPath, 
			uid);
		
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		Set<String> registeredProviderIds = socialConnectionRepositoryFactoryLocator.registeredProviderIds(promotionDeployPath);
		for (String registeredProviderId : registeredProviderIds) {
			connections.put(registeredProviderId, Collections.<Connection<?>>emptyList());
		}
		
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<Connection<?>>());
			}
			connections.add(providerId, connection);
		}
		return connections;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#findConnections(java.lang.String)
	 */
	public List<Connection<?>> findConnections(String providerId) {

		List<Connection<?>> connectionList = jdbcTemplate.query(
			SELECT_USER_CONNECTION_SQL, 
			connectionMapper, 
			promotionDeployPath, 
			providerId, 
			uid);
		
		return connectionList;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#findConnections(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		
		List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#findConnectionsToUsers(org.springframework.util.MultiValueMap)
	 */
	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
		
		// TODO: TDM: This method is totally confusing to me as to its purpose.  See if its needed.
		if (providerUsers == null || providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}
		/*
		StringBuilder providerUsersCriteriaSql = new StringBuilder();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("uid", uid);
		for (Iterator<Entry<String, List<String>>> it = providerUsers.entrySet().iterator(); it.hasNext();) {
			Entry<String, List<String>> entry = it.next();
			String providerId = entry.getKey();
			providerUsersCriteriaSql.append("PROVIDER_ID = :providerId_").append(providerId).append(" AND UID IN (:providerUserIds_").append(providerUserId).append(")");
			parameters.addValue("providerId_" + providerId, providerId);
			parameters.addValue("providerUserIds_" + providerId, entry.getValue());
			if (it.hasNext()) {
				providerUsersCriteriaSql.append(" OR " );
			}
		}
		
		List<Connection<?>> resultList = new NamedParameterJdbcTemplate(jdbcTemplate).query("SELECT PROMO_DEPLOY_PATH, PROVIDER_ID, PROVIDER_USER_ID, ACCESS_TOKEN, ACCESS_TOKEN_SECRET FROM SOCIAL_USER_CONNECTION WHERE UID = :uid AND " + providerUsersCriteriaSql + " ORDER BY PROVIDER_ID", parameters, connectionMapper);
		MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			List<String> uids = providerUsers.get(providerId);
			List<Connection<?>> connections = connectionsForUsers.get(providerId);
			if (connections == null) {
				connections = new ArrayList<Connection<?>>(uids.size());
				for (int i = 0; i < uids.size(); i++) {
					connections.add(null);
				}
				connectionsForUsers.put(providerId, connections);
			}
			String providerUserId = connection.getKey().getProviderUserId();
			int connectionIndex = uids.indexOf(providerUserId);
			connections.set(connectionIndex, connection);
		}
		
		return connectionsForUsers;
		*/
		throw new RuntimeException("Not implemented");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#getConnection(org.springframework.social.connect.ConnectionKey)
	 */
	public Connection<?> getConnection(ConnectionKey connectionKey) {
		
		try {
			return jdbcTemplate.queryForObject(
				SELECT_USER_CONNECTION_SQL, 
				connectionMapper, 
				promotionDeployPath, 
				connectionKey.getProviderId(), 
				uid);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchConnectionException(connectionKey);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#getConnection(java.lang.Class, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		
		String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#getPrimaryConnection(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		
		String providerId = getProviderId(apiType);
		Connection<A> connection = (Connection<A>)findPrimaryConnection(providerId);
		if (connection == null) {
			throw new NotConnectedException(providerId);
		}
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#findPrimaryConnection(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {

		String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#addConnection(org.springframework.social.connect.Connection)
	 */
	@Transactional
	public void addConnection(Connection<?> connection) {
		
		try {
			ConnectionData data = connection.createData();
			jdbcTemplate.update(INSERT_USER_CONNECTION_SQL,
				promotionDeployPath,				 
				data.getProviderId(),
				uid,
				encrypt(data.getAccessToken()), 
				encrypt(data.getSecret()));
		} catch (DuplicateKeyException e) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#updateConnection(org.springframework.social.connect.Connection)
	 */
	@Transactional
	public void updateConnection(Connection<?> connection) {

		ConnectionData data = connection.createData();
		jdbcTemplate.update(UPDATE_USER_CONNECTION_SQL,
			encrypt(data.getAccessToken()), 
			encrypt(data.getSecret()),
			promotionDeployPath,
			data.getProviderId(), 
			uid);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#removeConnections(java.lang.String)
	 */
	@Transactional
	public void removeConnections(String providerId) {
		
		jdbcTemplate.update(
			DELETE_USER_CONNECTION_SQL, 
			promotionDeployPath, 
			providerId, 
			uid);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.ConnectionRepository#removeConnection(org.springframework.social.connect.ConnectionKey)
	 */
	@Transactional
	public void removeConnection(ConnectionKey connectionKey) {
		
		jdbcTemplate.update(
			DELETE_USER_CONNECTION_SQL, 
			promotionDeployPath, 
			connectionKey.getProviderId(), 
			uid);		
	}

	/**
	 * 
	 * @param providerId
	 * @return
	 */
	private Connection<?> findPrimaryConnection(String providerId) {
		
		List<Connection<?>> connections = jdbcTemplate.query(
			SELECT_USER_CONNECTION_SQL, 
			connectionMapper, 
			promotionDeployPath, 
			providerId, 
			uid);
		
		if (connections.size() > 0) {
			return connections.get(0);
		} else {
			return null;
		}		
	}
	
	/*
	 * 
	 * @author Tom.Myers
	 *
	 */
	class ServiceProviderConnectionMapper implements RowMapper<Connection<?>> {
		
		/*
		 * (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@SuppressWarnings("unused")
		public Connection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			SocialConnectionData connectionData = mapConnectionData(rs);
			ConnectionFactory<?> connectionFactory = socialConnectionRepositoryFactoryLocator.getConnectionFactory(
				connectionData.getPromotionDeployPath(),
				connectionData.getProviderId());
			return connectionFactory.createConnection(connectionData);
		}

		/*
		 * 
		 * @param rs
		 * @return
		 * @throws SQLException
		 */
		private SocialConnectionData mapConnectionData(ResultSet rs) throws SQLException {
			
			String promotionDeployPath = rs.getString("PROMO_DEPLOY_PATH");
			String providerId = rs.getString("PROVIDER_ID");
			String providerUserId = "";
			String displayName = "";
			String profileUrl = "";
			String imageUrl = "";
			String accessToken = decrypt(rs.getString("ACCESS_TOKEN"));
			String accessTokenSecret = decrypt(rs.getString("ACCESS_TOKEN_SECRET"));
			String refreshToken = "";
			Long expireTime = Long.valueOf(0);
			
			return new SocialConnectionData(
				promotionDeployPath,
				providerId, 
				providerUserId, 
				displayName, 
				profileUrl, 
				imageUrl,
				accessToken, 
				accessTokenSecret, 
				refreshToken, 
				expireTime(expireTime));
		}

		/*
		 * 
		 * @param encryptedText
		 * @return
		 */
		private String decrypt(String encryptedText) {
			
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}
		
		/*
		 * 
		 * @param expireTime
		 * @return
		 */
		private Long expireTime(long expireTime) {
			
			return expireTime == 0 ? null : expireTime;
		}
	}

	/*
	 * 
	 * @param apiType
	 * @return
	 */
	private <A> String getProviderId(Class<A> apiType) {
		
		return socialConnectionRepositoryFactoryLocator.getConnectionFactory(this.promotionDeployPath, apiType).getProviderId();
	}
	
	/*
	 * 
	 * @param text
	 * @return
	 */
	private String encrypt(String text) {
		
		return text != null ? textEncryptor.encrypt(text) : text;
	}
}