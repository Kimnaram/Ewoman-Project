<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

  $sql = "select P.post_no as post_no, count(recommender) as post_like, P.title, content, name from post P left join post_like L on P.post_no = L.post_no, user U where P.email = U.email group by P.post_no, P.title, P.content, U.name";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0) {
	  
	  echo "리뷰가 없습니다.";
  
  }
  else {

   	$result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)) {

		extract($row);

                array_push($result,
                  array("post_no"=>$row["post_no"],
		  "title"=>$row["title"],
		  "content"=>$row["content"],
		  "name"=>$row["name"],
		  "post_like"=>$row["post_like"]
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
?>
