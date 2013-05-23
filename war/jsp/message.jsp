<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Fallen Galaxy</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<style type="text/css">
body { background-color: black; }

#preloading {
	position: absolute;
	top: 50%;
	left: 50%;
	width: 600px;
	margin: -50px 0 0 -300px;
	font-size: 16px;
	font-family: Lucida Grande, Bitstream Vera Sans, Verdana, sans-serif;
	vertical-align: middle;
	background-color: #202020;
	border: 1px solid #404040;
	padding: 10px;
	color: white;
}

#preloading a {
	color: #1fa1ff;
	text-decoration: none;
}

#preloading a:hover { text-decoration: underline; }
		</style>
	</head>
	<body>
		<div id="preloading">
			<div style="font-weight: bold;">Fallen Galaxy</div>
			<div id="preloadingStatus" style="color: #808080;"><%= request.getAttribute("message") %></div>
		</div>
	</body>
</html>
