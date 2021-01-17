<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$item_no=isset($_POST['item_no']) ? $_POST['item_no'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($item_no != "") {

  $csql = "delete from class using class join item on class.item_name = item.name where item.name = '$item_name';";
  $cstmt = $con->prepare($csql);
  $cstmt->execute();

  $sql="delete from item where item_no=$item_no";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  $selectsql="select * from item where item_no=$item_no";
  $stmt2 = $con->prepare($selectsql);
  $stmt2->execute();

  if ($stmt2->rowCount() == 0) {

        $result = "삭제 성공";
        echo $result;

        $altersql="alter table item auto_increment=1";
        $stmt = $con->prepare($altersql);
        $stmt->execute();

        $setsql="set @count = 0";
        $stmt = $con->prepare($setsql);
        $stmt->execute();

        $updatesql="update item set item_no = @count:=@count+1";
        $stmt = $con->prepare($updatesql);
        $stmt->execute();

  }
  else{

        $result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        	extract($row);

                array_push($result,
                   array("item_no"=>$row["item_no"]
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
	 ITEM_NAME : <input type = "text" name = "item_name" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
