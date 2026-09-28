-- phpMyAdmin SQL Dump
-- version 4.4.15.10
-- https://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Сен 28 2026 г., 16:51
-- Версия сервера: 5.5.68-MariaDB
-- Версия PHP: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `bdnlremakelauncher`
--

-- --------------------------------------------------------

--
-- Структура таблицы `bug_reports`
--

CREATE TABLE IF NOT EXISTS `bug_reports` (
  `id` int(11) NOT NULL,
  `uid` text COLLATE utf8mb4_unicode_ci,
  `location` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `error_text` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `timestamp` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_brand` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_model` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `android_version` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `android_sdk` int(11) NOT NULL,
  `app_version` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `network_type` text COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Дамп данных таблицы `bug_reports`
--

INSERT INTO `bug_reports` (`id`, `uid`, `location`, `error_text`, `timestamp`, `device_brand`, `device_model`, `android_version`, `android_sdk`, `app_version`, `network_type`) VALUES
(1, NULL, 'signInWithCredential() catch (ApiException e)', 'com.google.android.gms.common.api.ApiException: 12501: ', '2026-08-06 20:09:01', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(2, NULL, 'signInWithCredential() catch (ApiException e)', 'com.google.android.gms.common.api.ApiException: 12501: ', '2026-08-06 20:10:23', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(3, 'TvEipNqFOtSVdhnBAdK02DerJqW2', '(oAuth, fail)', 'Ошибка обработки токена User returns to auth activity without auth', '2026-08-20 14:05:21', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(4, 'TvEipNqFOtSVdhnBAdK02DerJqW2', '.createUserWithEmailAndPassword(email_layout_email_input', 'com.google.android.gms.tasks.zzw@c4a70e5', '2026-08-21 15:34:55', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(5, 'TvEipNqFOtSVdhnBAdK02DerJqW2', '.createUserWithEmailAndPassword(email_layout_email_input', 'com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.', '2026-08-21 15:36:47', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(6, 'TvEipNqFOtSVdhnBAdK02DerJqW2', '.createUserWithEmailAndPassword(email_layout_email_input', 'com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.', '2026-08-21 19:44:23', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(7, NULL, '.createUserWithEmailAndPassword(email_layout_email_input', 'com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.', '2026-08-21 19:47:15', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(8, NULL, '.createUserWithEmailAndPassword(email_layout_email_input', 'com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.', '2026-08-21 19:48:14', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(9, NULL, 'signInWithCredential() catch (ApiException e)', 'com.google.android.gms.common.api.ApiException: 12501: ', '2026-08-24 19:37:02', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(10, NULL, '(oAuth, fail)', 'Ошибка обработки токена User returns to auth activity without auth', '2026-08-24 19:39:05', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(11, 'iwEGq04Qi2MbzXGhfwCnGiBCB6G3', 'BaseDownloadTask - error()', 'java.net.SocketException: Software caused connection abort', '2026-08-29 19:45:55', 'samsung', 'SM-S947B', '16', 36, 'copymatrp-2.7.0.2.0-TEST16ANDROID', 'CELLULAR'),
(12, 'TvEipNqFOtSVdhnBAdK02DerJqW2', 'BaseDownloadTask - error()', 'java.net.SocketException: Software caused connection abort', '2026-08-30 16:24:09', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.2.1', 'WIFI'),
(13, 'TvEipNqFOtSVdhnBAdK02DerJqW2', 'sInterface.getServers(..)...', 'com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was BEGIN_ARRAY at line 2 column 6 path $[0]\nSee https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure', '2026-09-26 19:27:15', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.2.61', 'WIFI'),
(14, 'TvEipNqFOtSVdhnBAdK02DerJqW2', 'sInterface.getServers(..)...', 'com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was BEGIN_ARRAY at line 2 column 6 path $[0]\nSee https://github.com/google/gson/blob/main/Troubleshooting.md#unexpected-json-structure', '2026-09-26 20:02:38', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.2.61', 'WIFI');

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `bug_reports`
--
ALTER TABLE `bug_reports`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `bug_reports`
--
ALTER TABLE `bug_reports`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=15;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
