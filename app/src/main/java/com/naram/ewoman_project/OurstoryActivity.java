package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;

public class OurstoryActivity extends AppCompatActivity {

    private ViewFlipper vf_image_slide;
    private TextView tv_section2;
    private TextView tv_section5;
    private TextView tv_section8;
    private TextView tv_section11;
    private TextView tv_section14;
    private TextView tv_section17;
    private TextView tv_final;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ourstory);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GRAY)); //툴바 배경색

        initAllComponent();

        firebaseAuth = FirebaseAuth.getInstance();

        int images[] = {
                R.drawable.stry_slider_1,
                R.drawable.stry_slider_2
        };

        for(int image : images) {
            fllipperImages(image);
        }

        setTextInTextView();

    }

    private void initAllComponent() {

        vf_image_slide = findViewById(R.id.vf_image_slide);
        tv_section2 = findViewById(R.id.tv_section2);
        tv_section5 = findViewById(R.id.tv_section5);
        tv_section8 = findViewById(R.id.tv_section8);
        tv_section11 = findViewById(R.id.tv_section11);
        tv_section14 = findViewById(R.id.tv_section14);
        tv_section17 = findViewById(R.id.tv_section17);
        tv_final = findViewById(R.id.tv_final);

    }

    // 이미지 슬라이더 구현 메서드
    public void fllipperImages(int image) {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        vf_image_slide.addView(imageView);      // 이미지 추가
        vf_image_slide.setFlipInterval(3000);       // 자동 이미지 슬라이드 딜레이시간(1000 당 1초)
        vf_image_slide.setAutoStart(true);          // 자동 시작 유무 설정

        // animation
        vf_image_slide.setInAnimation(this,android.R.anim.slide_in_left);
        vf_image_slide.setOutAnimation(this,android.R.anim.slide_out_right);
    }

    public void setTextInTextView() {

        String section2 = "안녕하세요. e우먼 대표 정슬기입니다.\n\n" +
                "에디터와 광고 제작자로 수많은 프로젝트를 진행하면서 남들과 다른 '한 끝 차이'가 성공의 키라는 것을 배웠고 그 차이를 창조하는 최고의 스타, 사진가, 디자이너, 헤어 메이크업 아티스트, 스타일리스트들과 일해 왔습니다. 이들은 세계를 무대로 탄탄한 네트워크 안에서 배우고 밀고 끌어주면서 한국을 알리는 아름다운 씬(scene)을 만들어 내고 있습니다.\n\n" +
                "이와는 반대로 30대 전후, 결혼과 출산을 기점으로 사회 동기들이 눈에 띄게 줄어드는 것을 보았습니다. 기업 미팅에서 또래의 여성 실무자를 찾아보기 어려워졌고 육아 휴직을 마치고 복귀한 친구들은 일에도 육아에도 집중하기 어려운 환경에 처해 결국 퇴사를 권유 받거나 평범한 창업 시장에서 고군분투 하는 모습이 대부분 이었습니다.\n\n" +
                "이것이 제가 일하는 여성들에 대해 깊이 생각하게 된 시점이고 스스로 일어나 첫 발자국을 땐 이유입니다.";
        tv_section2.setText(section2);

        String section5 = "e우먼은 한국 최초로 일하는 여성들이 만나고 배우며 함께 성장하는 곳입니다. e우먼 college는 저희가 가장 오랜 시간 공들여 온 분야입니다. 답은 스스로 찾아내야 하지만 함께 배우는 과정을 통해 성과는 배가 되고 모두 성장합니다. 집단 지성의 진가는 여기서 발휘되며 e우먼이 다른 커뮤니티와 차별화 되는 핵심 가치입니다.";

        tv_section5.setText(section5);

        String section8 = "e우먼 라운지는 커뮤니티를 대표하는 소통의 장 입니다. 선한 영향력, 이해와 존중, 배움을 목표로 모인 각 분야 전문가는 물론, 모든 회원과 리더 그룹이 온라인과 오프라인의 경계를 넘어 다양한 주제를 가지고 서로의 관점에서 자신의 생각을 표현하고 아이디어를 내며 끊임없이 소통합니다.";

        tv_section8.setText(section8);

        String section11 = "색다른 관점을 통해 지속 가능한 성장 마인드셋을 설정하고 해왔던 일 그대로 성과로 연결합니다. 개인에게 브랜드 가치를 부여하고 맞춤형 컨텐츠로 가르칩니다. 우리 아이들을 위한 건강한 교육 환경을 연구하며 이웃을 배려하는 사회적 가치, 행복한 경험들을 컨텐츠로 만들어내는 방법을 실현해 냅니다.";

        tv_section11.setText(section11);

        String section14 = "e우먼은 철저한 멤버십을 통해 회원 개개인의 생각과 체험, 다양한 의견을 관리합니다. 이를 통해 고객의 입장에서 꼭 필요한 상품과 서비스를 공유하고 나아가 지역사회에서, 일하는 여성들을 중심으로 한 스마트 컨슈머의 의견을 가치로 창출하는 것을 목표로 합니다.";

        tv_section14.setText(section14);

        String section17 = "여성만을 위해 특화된 컨텐츠를 발굴하고 창업으로 연결, 함께 성장하는 커뮤니티는 아직 한국에 없습니다. e우먼은 주기적인 모임과 다양한 강연들을 통해 온라인과 오프라인의 경계를 허물고 CEO와 프로패셔널, 스마트 컨슈머들을 잇고자 합니다. 함께 배우고 연대하며 모두의 시장에 뛰어드는 것이 아닌, 우리를 중심으로 한 새로운 시장을 창조합니다.";

        tv_section17.setText(section17);

        String finaltext = "세계를 무대로 혼자 할 수 없는 것들을 나누어 해내고 서로를 이해하는 소통 커뮤니티, 이웃을 돌보는 리더십으로 성원에 보답하겠습니다. \n" +
                "\n" +
                "\n" +
                "고맙습니다. \n" +
                "\n" +
                "\n" +
                "2020. 05. 30";

        tv_final.setText(finaltext);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(firebaseAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login:
                Intent stry_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(stry_to_login);
                return true;
            case R.id.menu_signup:
                Intent stry_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(stry_to_signup);
                return true;
            case R.id.menu_logout:

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(OurstoryActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_stry = new Intent(getApplicationContext(), OurstoryActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_stry);
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
