<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

	$item_no = $_POST['item_no'];
        $email = $_POST['email'];

        if(empty($item_no)){
            $errMSG = "item_no 번호를 입력하세요.";
        }
        else if(empty($email)) {
            $errMSG = "이메일이 없습니다.";
        }

        if(!isset($errMSG))
        {
            try{
                $stmt = $con->prepare('INSERT INTO wishlist(item_no, email) VALUES(:item_no, :email)');
		$stmt->bindParam(':item_no', $item_no);
		$stmt->bindParam(':email', $email);

                if($stmt->execute())
                {
                    $successMSG = "상품을 Wishlist에 추가했습니다.";
                }
                else
                {
                    $errMSG = "위시 리스트 추가 에러";
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
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
