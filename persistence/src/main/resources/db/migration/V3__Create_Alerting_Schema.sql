CREATE TABLE `account` (
    `id` long PRIMARY KEY,
    `name` varchar(255) NOT NULL,
    `ext_id` varchar(255) NOT NULL
);

INSERT INTO `account`
VALUES (1, 'Test Account 1', '1111'),
       (2, 'Test Account 2', '2222');

CREATE TABLE `alert` (
    `id` long PRIMARY KEY AUTO_INCREMENT,
    `ext_id` varchar(255) UNIQUE NOT NULL,
    `name` varchar(255),
    `threshold` varchar(255),
    `type` ENUM ('HARDCODED_ALERT_1', 'HARDCODED_ALERT_2'),
    `frequency` varchar(255),
    `enabled` boolean default false,
    `last_trigger_timestamp` timestamp,
    `notification_sent_timestamp` timestamp,
    `is_triggered` boolean default false,
    `reference_time_period` varchar(255),
    `created` timestamp,
    `updated` timestamp,
    `updated_by` varchar(255),
    `account_id` long NOT NULL
);

INSERT INTO `alert`
VALUES (1, '1111-alert-1', 'Some Test Alert for Account 1111', '90', 'HARDCODED_ALERT_1', '15m', true, '2021-04-22 16:33:21.959', '2021-04-22 16:33:21.959', false, '1 Month', '2021-04-22 16:33:21.959', '2021-04-22 16:33:21.959', 'someUser1', 1),
(2, '1111-alert-2', 'Some Other Test Alert for Account 1111', '666', 'HARDCODED_ALERT_2', '5m', true, null, null, false, '1 Week', '2021-04-22 16:33:21.959', '2021-04-26 04:01:13.448', 'someUser1', 1),
(3, '2222-alert-1', 'Some Test Alert for Account 2222', '90', 'HARDCODED_ALERT_1', '15m', true, '2021-04-22 16:33:21.959', '2021-04-22 16:33:21.959', false, '1 Month', '2021-04-22 16:33:21.959', '2021-04-22 16:33:21.959', 'someOtherUser2', 2),
(4, '2222-alert-2', 'Some Other Test Alert for Account 2222', '666', 'HARDCODED_ALERT_2', '5m', true, null, null, false, '1 Week', '2021-04-22 16:33:21.959', '2021-04-26 04:01:13.448', 'someOtherUser2', 2);

ALTER TABLE `alert` ADD FOREIGN KEY (`account_id`) REFERENCES `account` (`id`);

CREATE TABLE `recipient` (
    `id` long PRIMARY KEY AUTO_INCREMENT,
    `ext_id` varchar(255) UNIQUE NOT NULL,
    `recipient` varchar(255) NOT NULL,
    `type` ENUM ('EMAIL', 'SMS', 'HTTP'),
    `username` varchar(255),
    `password` varchar(255),
    `created` timestamp NOT NULL,
    `updated` timestamp NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    `alert_id` long,
    `account_id` long
);

INSERT INTO `recipient`
VALUES (1, 'someUsersEmail-account-1111', 'someUser1@somedomain.com', 'EMAIL', null, null, '2021-02-22 16:09:28.139', '2021-02-22 16:09:28.139', 'someUser1', 1, 1),
(2, 'someOtherUsersEmail-account-1111', 'someOtherUser1@somedomain.com', 'EMAIL', null, null, '2021-03-04 07:17:33.244', '2021-03-04 08:00:54.562', 'someOtherUser1', 1, 1),
(3, 'someUserHttpRecipient-account-1111', 'https://some.test.callback.com/callback1', 'HTTP', 'mikeRoweSoft', 'iSuPpOrTwInDoWs', '2021-05-19 02:39:12.922', '2021-05-19 02:39:12.922', 'someUser1', 2, 1),
(4, 'someUserHttpRecipient-account-2222', 'https://some.test.callback.com/callback2', 'HTTP', 'teriDactyl', 'L1f3F1nd$AWay', '2021-05-19 02:39:12.922', '2021-05-19 02:39:12.922', 'someUser2', 2, 2),
(5, 'someUsersEmail-account-2222', 'someUser2@somedomain.com', 'EMAIL', null, null, '2021-02-22 16:09:28.139', '2021-02-22 16:09:28.139', 'someUser2', 1, 2),
(6, 'someOtherUsersSms-account-2222', '13035552222', 'SMS', null, null, '2021-02-22 16:09:28.139', '2021-02-22 16:09:28.139', 'someOtherUser2', 1, 2);

ALTER TABLE `recipient` ADD FOREIGN KEY (`alert_id`) REFERENCES `alert` (`id`);

ALTER TABLE `recipient` ADD FOREIGN KEY (`account_id`) REFERENCES `account` (`id`);

CREATE SEQUENCE `hibernate_sequence` START WITH 5 INCREMENT BY 1;