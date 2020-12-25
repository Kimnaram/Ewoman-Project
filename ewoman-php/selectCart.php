<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$email=isset($_POST['email']) ? $_POST['email'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($email != ""){

  $sql="select C.item_no, name, price, image, count, date from item I, cart C where I.item_no = C.item_no AND email='$email'";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "";
        echo $email;
        echo "이 카트에 담은 것이 없습니다.";
  }
        else{

                $result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

                extract($row);

                array_push($result,
                        array("name"=>$row["name"]
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
         EMAIL : <input type = "text" name = "email" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}

?>
