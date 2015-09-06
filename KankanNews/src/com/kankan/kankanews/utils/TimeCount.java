package com.kankan.kankanews.utils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class TimeCount {
//	public class CountdownTimerActivity extends Activity {
//		private TimeCount time;
//		private Button checking;
//		@Override
//		protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//		time = new TimeCount(60000, 1000);//构造CountDownTimer对象
//		checking = (Button) findViewById(R.id.button1);
//		checking.setOnClickListener(new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//		time.start();//开始计时
//		}
//		});
//		}
//		/* 定义一个倒计时的内部类 */
//		class TimeCount extends CountDownTimer {
//		public TimeCount(long millisInFuture, long countDownInterval) {
//		super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
//		}
//		@Override
//		public void onFinish() {//计时完毕时触发
//		checking.setText("重新验证");
//		checking.setClickable(true);
//		}
//		@Override
//		public void onTick(long millisUntilFinished){//计时过程显示
//		checking.setClickable(false);
//		checking.setText(millisUntilFinished /1000+"秒");
//		}
//		}
//	http://www.cnblogs.com/-cyb/archive/2011/12/18/Android_CountDownTimer.html
}
