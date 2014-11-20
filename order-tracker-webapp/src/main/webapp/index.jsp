<html>
<head>
<script type="text/javascript"> 

function callUrl(url, method) {
  var client = new XMLHttpRequest();
  client.open(method, url, false);
  client.setRequestHeader("Content-Type", "text/plain");
  client.send();
  if (client.status == 200)
    alert("The request succeeded!\n\nThe response representation was:\n\n" + client.responseText)
  else
    alert("The request did not succeed!\n\nThe response status was: " + client.status + " " + client.statusText + ".");
} 

</script>
</head>
<body>

<h2>Order tracker</h2>
<ul>
	<li>Try this <a href="rest/bangara">link</a></li>
	<li><a href="#" onclick="callUrl('rest/order/123/create?description=an order&fromAddress=12 Something St, Canberra&toAddress=144 Bank St, Dickson&comment=created&destinationEmail=recipient@goog.com&senderEmail=online.company@goog.com&maxAttempts=3','PUT');">Create an order</a></li>
</ul>


<p>Time now is <%= new java.util.Date() %></p>

</body>
</html>