function chat()
	return("Пошарив по карманам, вы ничего там не нашли.");
end

function help(...)
	client:send("Команда: инвентарь\n\tС помощью той команды вы можете получить информацию о переносимых вещах.");
end
