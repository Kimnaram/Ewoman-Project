<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$postId=isset($_POST['postId']) ? $_POST['postId'] : '';
$uid=isset($_POST['uid']) ? $_POST['uid'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($postId != ""){

  $sql="select count(*) from recommend_review where postId=$postId and uid='$uid'";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "";
        echo $postId;
        echo ", ";
        echo $uid;
        echo "는 찾을 수 없습니다.";

  }
	else{

   		$result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        	extract($row);

            array_push($result,
                array("count(*)"=>$row["count(*)"]
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
    echo "Reivew : ";
}
?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         Post ID : <input type = "text" name = "postId" />
         UID : <input type = "text" name = "uid" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
