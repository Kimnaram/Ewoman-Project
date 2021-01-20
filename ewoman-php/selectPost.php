<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$post_no=isset($_POST['post_no']) ? $_POST['post_no'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($post_no != ""){

  $sql="select post_no, title, image, content, name from post P, user U where P.email = U.email and post_no = $post_no";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "";
        echo $post_no;
        echo "은 찾을 수 없습니다.";
  }
  else{

	$result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
		
		extract($row);
		
		array_push($result,
			array("post_no"=>$row["post_no"],
                	"title"=>$row["title"],
                	"content"=>$row["content"],
                	"name"=>$row["name"],
			"image"=>$row["image"]
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
         post_no: <input type = "text" name = "post_no" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
