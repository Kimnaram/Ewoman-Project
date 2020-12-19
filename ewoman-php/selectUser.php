<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$email=isset($_POST['email']) ? $_POST['email'] : '';
$password=isset($_POST['password']) ? $_POST['password'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($email != "" && $password != ""){

  $sql="select name from user where email='$email' and password='$password'";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "";
        echo $email;
        echo "은 찾을 수 없습니다.";
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
    echo "User : ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         email : <input type = "text" name = "email" />
         password : <input type= "text" name = "password" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>

