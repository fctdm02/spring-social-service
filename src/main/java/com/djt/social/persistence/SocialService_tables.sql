-- Use the following command to create the schema itself: 
-- grant all on social.* to 'social'@'%' identified by 'socialtest';
-- grant all on social.* to 'root'@'%' identified by 'password';
--
-- Used to store the API Keys for the various social media providers for a given promotion.
CREATE TABLE IF NOT EXISTS SOCIAL_PROVIDER_API_KEY (
    PROMO_DEPLOY_PATH varchar(128) not null,
    PROVIDER_ID varchar(128) not null,
    API_KEY varchar(255) not null,
    API_KEY_SECRET varchar(255),
    PRIMARY KEY (PROMO_DEPLOY_PATH, PROVIDER_ID));

-- Used to store the access token for a given user for a given social media provider for a given promotion. 	
CREATE TABLE IF NOT EXISTS SOCIAL_USER_CONNECTION (
    PROMO_DEPLOY_PATH varchar(128) not null,
    PROVIDER_ID varchar(128) not null,
    UID varchar(128) not null,
    ACCESS_TOKEN varchar(255) not null,
    ACCESS_TOKEN_SECRET varchar(255),
    PRIMARY KEY (PROMO_DEPLOY_PATH, PROVIDER_ID, UID));
