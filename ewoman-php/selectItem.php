<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$item_no=isset($_POST['item_no']) ? $_POST['item_no'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($item_no != ""){

  $sql="select I.item_no, I.category, I.name, I.image, I.price, I.inform, I.deliv_method, I.deliv_price, I.deliv_inform, I.minimum_quantity, I.maximum_quantity, count(email) as wishlist from item I left join wishlist W on I.item_no = W.item_no where I.item_no=$item_no group by I.item_no";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "";
        echo $item_no;
        echo "라는 상품이 존재하지 않습니다.";
  }
        else{

                $result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

                extract($row);

                array_push($result,
                        array("category"=>$row["category"],
                                      "name"=>$row["name"],
                                      "image"=>$row["image"],
                                      "price"=>$row["price"],
                                      "inform"=>$row["inform"],
                                      "deliv_method"=>$row["deliv_method"],
                                      "deliv_price"=>$row["deliv_price"],
                                      "deliv_inform"=>$row["deliv_inform"],
                                      "minumum_quantity"=>$row["minimum_quantity"],
                                      "maximum_quantity"=>$row["maximum_quantity"],
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
         ITEM_NO : <input type = "text" name = "item_no" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}
?>
