function chat(...)
	client:send("Обновляемся...");
	server:update();
	client:send("Обновились.");
end

function help(...)
	client:send("Команда: обновить\n\tЗагружает по-новой всю информацию - настройки, скрипты, карту, словарь и прочее. Равносильно перезапуску игры, но без потери текущих результатов.");
end
