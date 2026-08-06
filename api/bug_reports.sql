-- phpMyAdmin SQL Dump
-- version 4.4.15.10
-- https://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Авг 06 2026 г., 20:22
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Дамп данных таблицы `bug_reports`
--

INSERT INTO `bug_reports` (`id`, `uid`, `location`, `error_text`, `timestamp`, `device_brand`, `device_model`, `android_version`, `android_sdk`, `app_version`, `network_type`) VALUES
(1, NULL, 'signInWithCredential() catch (ApiException e)', 'com.google.android.gms.common.api.ApiException: 12501: ', '2026-08-06 20:09:01', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI'),
(2, NULL, 'signInWithCredential() catch (ApiException e)', 'com.google.android.gms.common.api.ApiException: 12501: ', '2026-08-06 20:10:23', 'OnePlus', 'CPH2709', '15', 35, 'copymatrp-2.7.0.1.5', 'WIFI');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
