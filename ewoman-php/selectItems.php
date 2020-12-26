<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$category=isset($_POST['category']) ? $_POST['category'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($category != ""){

  $sql="select I.item_no, I.name, I.image, I.price, count(*) as wishlist from item I, wishlist W where I.item_no = W.item_no and category='$category' group by I.item_no";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "결과가 없습니다.";

  } else{

        $result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

            extract($row);

            array_push($result,
                        array("item_no"=>$row["item_no"],
                                    "name"=>$row["name"],
                                    "image"=>$row["image"],
                                    "price"=>$row["price"],
                                    "wishlist"=>$row["wishlist"]
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
    echo "Item : ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         CATEGORY : <input type = "text" name = "category" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}

?>
