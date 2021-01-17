<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$item_no=isset($_POST['item_no']) ? $_POST['item_no'] : '';
$class_name=isset($_POST['class_name']) ? $_POST['class_name'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($item_no != ""){

  $sql = "select I.item_no as item_no, category, I.name as name, I.price as price, image, deliv_price, IFNULL(C.name, '') as class_name, C.price as class_price 
from item I left join class C 
 on I.name = C.item_name 
where I.item_no in $item_no and (C.name is null or C.name in $class_name)";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "";
        echo $item_no;
        echo "주문한 상품이 존재하지 않습니다.";
  }
        else{

               $result = array();

               while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

                 extract($row);

                 array_push($result,
			 array("item_no"=>$row["item_no"],
			 "category"=>$row["category"],
			 "name"=>$row["name"],
			 "price"=>$row["price"],
			 "image"=>$row["image"],
			 "deliv_price"=>$row["deliv_price"],
			 "class_name"=>$row["class_name"],
			 "class_price"=>$row["class_price"]
                 ));
        }


        if (!$android) {
          $json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
          echo $json;
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }
}
else {
    echo "Order : ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         item_no : <input type = "text" name = "item_no" />
         class_name : <input type = "text" name = "class_name" />
	 <input type = "submit" />
      </form>

   </body>
</html>
<?php
}

?>
