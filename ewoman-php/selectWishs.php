<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$item_no=isset($_POST['item_no']) ? $_POST['item_no'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($item_no != ""){

  $sql="select count(*) as wishlist from wishlist where item_no=$item_no group by item_no";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "";
        echo $item_no;
        echo "의 wishlist는 없습니다.";
  } else {

	$result = array();

	while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

		extract($row)

		array_push($result,
                		array("wishlist"=>$row["wishlist"],
                	));
	}


	if (!$android) {
		$json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
		echo $json;
	} else {
		header('Content-Type: application/json; charset=utf8');
		$json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
		echo $json;
	}
   }
}
else {
    echo "Wishlist : ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android) {
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         item_no : <input type = "text" name = "item_no" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
