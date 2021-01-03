<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
{

	$item_no = $_POST['item_no'];
	$category = $_POST['category'];
	$name = $_POST['name'];
	$price = $_POST['price'];
	$image = $_POST['image'];
	$inform = $_POST['inform'];
	$deliv_method = $_POST['deliv_method'];
	$deliv_price = $_POST['deliv_price'];
	$deliv_inform = $_POST['deliv_inform'];
	$minimum_quantity = $_POST['minimum_quantity'];
	$maximum_quantity = $_POST['maximum_quantity'];

        if(empty($category)) {
		$errMSG = "카테고리를 입력해야 합니다.";
	} else if(empty($name)){
		$errMSG = "상품 이름을 입력해야 합니다.";
	} else if(empty($price)) {
		$errMSG = "상품 가격을 입력해야 합니다.";
	} else if(empty($image)) {
		$errMSG = "상품 이미지를 저장해야 합니다.";
	}

	if(!isset($errMSG))	{
		try {
			$stmt = $con->prepare("UPDATE item SET category=:category, name=:name, price=$price, image=:image, inform=:inform, deliv_method=:deliv_method, deliv_price=$deliv_price, deliv_inform=:deliv_inform, minimum_quantity=$minimum_quantity, maximum_quantity=$maximum_quantity WHERE item_no=:item_no");

			$stmt->bindParam(':category', $category);
			$stmt->bindParam(':name', $name);
			#$stmt->bindParam(':price', $price);
			$stmt->bindParam(':image', $image);
			$stmt->bindParam(':inform', $inform);
			$stmt->bindParam(':deliv_method', $deliv_method);
			#$stmt->bindParam(':deliv_price', $deliv_price);
			$stmt->bindParam(':deliv_inform', $deliv_inform);
			#$stmt->bindParam(':minimum_quantity', $minimum_quantity);
			#$stmt->bindParam(':maximum_quantity', $maximum_quantity);
			$stmt->bindParam(':item_no', $item_no);

			if($stmt->execute()) {
				$successMSG = "상품의 내용을 수정했습니다.";
			} else {
				$errMSG = "상품 추가 에러";
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
		ITEM_NO : <input type= "text" name = "item_no" />
                CATEGORY : <input type = "text" name = "category" />
                NAME : <input type = "text" name = "name" />
                PRICE : <input type = "text" name = "price" />
                IMAGE : <input type = "text" name = "image" />
                INFORM : <input type = "text" name = "inform" />
                D_METHOD : <input type = "text" name = "deliv_method" />
                D_PRICE : <input type = "text" name = "deliv_price" />
                D_INFORM : <input type = "text" name = "deliv_inform" />
                MIN_QUANTITY : <input type = "text" name = "minimum_quantity" />
                MAX_QUANTITY : <input type = "text" name = "maximum_quantity" />
                <input type = "submit" name = "submit" />
            </form>

   </body>
</html>

<?php
}
?>
