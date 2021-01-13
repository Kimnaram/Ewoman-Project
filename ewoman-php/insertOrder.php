<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
	$item_no = $_POST['item_no'];
	$email = $_POST['email'];
	$count = $_POST['count'];
	$class_name = $_POST['class_name'];

	if(empty($item_no)) {
		$errMSG = "상품 번호 부재";
	} else if(empty($email)){
		$errMSG = "사용자 이메일 부재";
	} else if(empty($count)) {
		$errMSG = "상품 수량 부재";
	}

	if(!isset($errMSG))	{
		try {
			$stmt = $con->prepare('INSERT INTO `order`(item_no, email, count, class_name) VALUES(:item_no, :email, :count, :class_name)');
			$stmt->bindParam(':item_no', $item_no);
			$stmt->bindParam(':email', $email);
			$stmt->bindParam(':count', $count);
			$stmt->bindParam(':class_name', $class_name);

			if($stmt->execute()) {
				$successMSG = "상품을 주문했습니다.";
			} else {
				$errMSG = "주문 에러";
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
                ITEM_NO : <input type = "text" name = "item_no" />
		EMAIL : <input type = "text" name = "email" />
		DATE : <input type = "text" name = "date" />
                COUNT : <input type = "text" name = "count" />
		CLASS_NAME : <input type = "text" name = "class_name" />
                <input type = "submit" name = "submit" />
            </form>


       </body>
    </html>

<?php
    }
?>
