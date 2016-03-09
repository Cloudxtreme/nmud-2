function chat()
	x1 = 10; y1 = 30; -- координаты точки А, "севера"
	x2 = 55; y2 = 12; -- координаты точки В
	x3 = 10; y3 = 20; -- координаты точки С
	print("Точки:\n\tA("..x1..","..y1..")\n\tB("..x2..","..y2..")\n\tC("..x3..","..y3..")");

	l1 = math.sqrt((x2-x1)^2+(y2-y1)^2); -- расстояние AB
	l2 = math.sqrt((x2-x3)^2+(y2-y3)^2); -- расстояние BC
	l3 = math.sqrt((x3-x1)^2+(y3-y1)^2); -- расстояние AC

	print("Длины:\n\tДлина 1 (AB) = "..l1.."\n\tДлина 2 (BC) = "..l2.."\n\tДлина 3 (AC) = "..l3);

	cos = (l1^2 + l2^2 - l3^2) / (2 * l1 * l2); -- косинус
	sin = math.sqrt(1-cos^2);
	tg = sin / cos;
	arctg = math.atan(tg);
	arccos = math.acos(cos);
	print("Соотношения сторон:\n\tCинус: "..sin.."\n\tKocинус: "..cos.."\n\tТангенс: "..tg.."\n\tАрккосинус: "..arccos);

--	sample = math.acos(((x1 - x2) * (x3 - x2) + (y1 - y2) * (y1 - y2)) / (l1 * l2));
--	print("Пример с сайта дает сразу:\n\t"..sample);

	res = 180/math.pi * arccos;
	print("Градусная мера:\n\t"..res);
end
