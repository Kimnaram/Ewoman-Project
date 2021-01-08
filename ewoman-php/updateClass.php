<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

	$class = $_POST['class'];

        if(empty($class)){
            $errMSG = "class 정보가 없습니다.";
        }

        if(!isset($errMSG))
	{
		echo $class;
		$json_string = $class;
		$json = json_decode($json_string, true);

		foreach($json['class'] as $key=>$value) {
			$item_name = $value['itemName'];
			$name = $value['className'];
			$price = $value['classPrice'];

			try{
				$stmt = $con->prepare('UPDATE class SET name=:name, price=:price where item_name=:item_name');
				$stmt->bindParam(':item_name', $item_name);
				$stmt->bindParam(':name', $name);
				$stmt->bindParam(':price', $price);

				#print_r($stmt);

				if($stmt->execute()) {
		    			$successMSG = "클래스 정보를 수정했습니다.";
				}
				else {
		    			$errMSG = "클래스 수정 에러";
				}
			} catch(PDOException $e) {
				die("Database error: " . $e->getMessage());
			}

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
                CLASS : <input type = "text" name = "class" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
