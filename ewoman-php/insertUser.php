<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

        $email = $_POST['email'];
        $password = $_POST['password'];
        $name = $_POST['name'];
        $gender = $_POST['gender'];
        $phone = $_POST['phone'];

        if(empty($email)){
            $errMSG = "이메일을 입력하세요.";
        }
        else if(empty($password)) {
            $errMSG = "비밀번호를 입력하세요.";
        }
        else if(empty($name)) {
            $errMSG = "이름을 입력하세요.";
        }
        else if(empty($phone)){
            $errMSG = "전화번호를 입력하세요.";
        }

        if(!isset($errMSG))
        {
            try{
                $stmt = $con->prepare('INSERT INTO user(email, password, name, gender, phone) VALUES(:email, :password, :name, :gender, :phone)');
	$stmt->bindParam(':email', $email);
	$stmt->bindParam(':password', $password);
	$stmt->bindParam(':name', $name);
                $stmt->bindParam(':gender', $gender);
                $stmt->bindParam(':phone', $phone);

                if($stmt->execute())
                {
                    $successMSG = "새로운 사용자를 추가했습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
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
                EMAIL : <input type = "text" name = "email" />
                PASSWORD : <input type = "text" name = "password" />
                NAME : <input type = "text" name = "name" />
                GENDER : <input type = "text" name = "gender" />
                PHONE : <input type = "text" name = "phone" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
