<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
	$item_name = $_POST['item_name'];
	$name = $_POST['name'];
	$price = $_POST['price'];
	
        	if(empty($item_name)) {
		$errMSG = "상품 이름 부재";
	} else if(empty($name)){
		$errMSG = "클래스 이름 부재";
	} else if(empty($price)) {
		$errMSG = "클래스 가격 부재";
	}

	if(!isset($errMSG))	{
		try {

			$sql="select item_no from item where name='$item_name'";
			$pstmt = $con->prepare($sql);
			$pstmt->execute();

			if ($pstmt->rowCount() == 0){

			        echo "";
			        echo $item_name;
			        echo "이 존재하지 않습니다.";
			}
 			else{

			        $result = array();

			        while($row=$pstmt->fetch(PDO::FETCH_ASSOC)){

			                extract($row);

				$item_no = $row["item_no"];

				$stmt = $con->prepare("INSERT INTO class(item_no, name, price) VALUES($item_no, :name, $price)");
				$stmt->bindParam(':name', $name);

				if($stmt->execute()) {
					$successMSG = "상품의 옵션을 추가했습니다.";
				} else {
					$errMSG = "옵션 추가 에러";
				}
        			}

		} catch(PDOException $e) {
			die("Database error: " . $e->getMessage());
		}
	}
    }

?>


<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

	$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( !$android )
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                ITEM_NAME : <input type = "text" name = "item_name" />
                NAME : <input type = "text" name = "name" />
                PRICE : <input type = "text" name = "prcie" />
                <input type = "submit" name = "submit" />
            </form>


       </body>
    </html>

<?php
    }
?>
