<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$item_no=isset($_POST['item_no']) ? $_POST['item_no'] : '';
$email=isset($_POST['email']) ? $_POST['email'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($item_no != "" && $email != ""){

  $sql="delete from cart where item_no in ($item_no) and email='$email'";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  $selectsql="select * from cart where item_no=$item_no and email='$email'";
  $stmt2 = $con->prepare($selectsql);
  $stmt2->execute();

  if ($stmt2->rowCount() == 0) {

        $result = "삭제 성공";
        echo $result;
  }
  else{

        $result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        	extract($row);

                array_push($result,
                   array("item_no"=>$row["item_no"],
                   "email"=>$row["email"],
                   "count"=>$row["name"],
                   "date"=>$row["uid"]
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
    echo "Cart : ";
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
         EMAIL : <input type = "text" name = "email" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
