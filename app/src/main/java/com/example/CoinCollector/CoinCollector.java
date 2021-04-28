package com.example.CoinCollector;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class CoinCollector extends AppCompatActivity {
    /**
     * EndlessRunner - with modifications by...
     *
     * Login ID - irisz, Rami1, ashley68
     * Student ID - 215323579,
     * name - Yahui Zhao, Rumei Mai, Yu Liang
     */

    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth; //Layout of the menu & game
    private LinearLayout startLayout;

    float startTime,totoalTime,spendTime, dt;
    // Image
    private ImageView sprite, spike, coin, heart; //image variables

    private ImageView bottomCheck;
    // Size
    private int spriteSize;

    // Position
    private float spriteX, spriteY;
    private float spikeX, spikeY;
    private float coinX, coinY;
    private float heartX, heartY;

    // Score
    private TextView scoreLabel, totalLabel, timeLabel;
    private int score, highScore, timeCount, coinTotal;

    public float timeSecond;

    private SharedPreferences settings;

    // Class
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;

    // Status
    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean heart_flg = false;

    //speed
    private int spriteSpeed;
    private int coinSpeed;
    private int heartSpeed;
    private int spikeSpeed;

    public int screenWidth;
    public int screenHeight;
    public int difficulty;

    private ImageButton tv_left,tv_right;


    private boolean left = true;
    private boolean center = false;
    private boolean right = false;
    boolean isSwape=false;


    /**
     * This method creates the activity and initialises all of the objects/sounds.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC); //the volume buttons on the phone dictate the volume of the application, when it is visible.

        gameFrame = findViewById(R.id.gameFrame);
        startLayout = findViewById(R.id.startLayout);
        sprite = findViewById(R.id.sprite);

        coin = findViewById(R.id.coin);
        heart = findViewById(R.id.heart);
        scoreLabel = findViewById(R.id.scoreLabel);
        timeLabel = findViewById(R.id.timeLabel);
//        highScoreLabel = findViewById(R.id.highScoreLabel);
        totalLabel = findViewById(R.id.totalLabel);
        tv_left = findViewById(R.id.tv_left);
        tv_right = findViewById(R.id.tv_right);

        bottomCheck = findViewById(R.id.buttom_check);

        startTime=0;
        totoalTime=0;
        spendTime=0;
        dt = 0;
        //     coins.add((ImageView)findViewById(R.id.coin));

        // High Score
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        //   highScore = settings.getInt("HIGH_SCORE", 0);
        //     highScoreLabel.setText("High Score : " + highScore);

        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //Nexus 5x w 1080 h 1920 dpi 420
        //heart -20 spike -100 sprite +14 coin -100
        spriteSpeed = Math.round(screenHeight / 137F);
        coinSpeed = Math.round(screenWidth / -10.8F);
        heartSpeed = Math.round(screenWidth / -54F);
        spikeSpeed = Math.round(screenWidth / -10.8F);

        Log.v("spriteSpeed", spriteSpeed + "");
        Log.v("coinSpeed", coinSpeed + "");
        Log.v("heartSpeed", heartSpeed + "");

        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(right){
                    left=false;
                    center=true;
                    right=false;
                }else if(center) {
                    left=true;
                    center=false;
                    right=false;
                }
            }
        });

        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(left){
                    left=false;
                    center=true;
                    right=false;
                }else if(center) {
                    left=false;
                    center=false;
                    right=true;
                }
            }
        });
    }

    /**
     * This is used to consistenly update the game.
     * Objects are also modified depending on the difficulty.
     * Sound will play if character model and objects collide.
     */
    public void changePos() {

        //set bottomCheck



        // Add timeCount
        timeCount += 10; //every 10 milliseconds the timer will call the changePos method, therefore, udpating everything.


        if(score ==25){
            totoalTime= System.nanoTime();
            spendTime = totoalTime - startTime;
            dt = (spendTime) / 1000000000f;
            Log.v("gameFinish", String.valueOf(dt));
            gameOver();
        }
        //   timeSecond = timeCount/1000;

        coinY += 10; //speed of how fast the coins fall down.

        float coinCenterX = coinX + coin.getWidth() / 2;
        float coinCenterY = coinY + coin.getHeight() / 2;


        if (bottomGroundCheck(coinCenterX, coinCenterY)) {
            switch (difficulty) {
                case 1:
                    coinTotal += 1;
                    Log.v("cointotal: ", String.valueOf( coinTotal));
                    break;
                case 2:
                    coinTotal += 1;
                    Log.v("cointotal: ", String.valueOf( coinTotal));
                    break;

            }

        }

        if (hitCheck(coinCenterX, coinCenterY)) {
            coinY = frameHeight + 100;


            //modifying objects based on game difficulty.
            switch (difficulty) {
                case 1:
                    score += 1;
                    break;
                case 2:
                    score += 1;
                    break;

            }

            soundPlayer.playHitcoinSound();
        }

        if (coinY > frameHeight) {
            coinY = -50;
            //creat a list to store the three position for coinX
            List<Integer> list = new ArrayList<>();
            list.add(0);
            list.add((frameWidth/2)-(spriteSize/2));
            list.add(frameWidth - spriteSize);
            Random r = new Random();
            coinX = list.get(r.nextInt(list.size()));

        }
        coin.setX(coinX);
        coin.setY(coinY);

        switch (difficulty) {
            case 1:
                coinY += 15; //speed of how fast the spikes fall.
                break;
            case 2:
                coinY += 24; //speed of how fast the spikes fall.
                break;

        }





        if (left) {
            spriteX = 0;
        } else if (center) {
            spriteX=frameWidth/2-spriteSize/2;
        }else if(right){
            spriteX=frameWidth - spriteSize;
        }
        sprite.setX(spriteX);

        scoreLabel.setText("Score : " + score); //score text at the top.
        totalLabel.setText("Total : "+ coinTotal);
        timeLabel.setText("Time : "+ dt +"s");



    }

    /**
     * Detects if my characters hitbox has been hit.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean hitCheck(float x, float y) {  //detects whether the character has been hit.
        if (spriteX <= x && x <= spriteX + spriteSize &&  //detects width of the character.
                spriteY <= y && y <= frameHeight) { //detects height of the character.
            return true;
        }
        return false;
    }
    public boolean bottomGroundCheck(float x, float y){
        if( y <= bottomCheck.getHeight() && y<=frameHeight){
            return true;

        }   return false;

    }

    public void changeFrameWidth(int frameWidth) {
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);
    }

    /**
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * When the game ends timer is cancelled, therefore, the main menu becomes visible and the game stops.
     * score and Highscore are saved and updated here.
     */
    public void gameOver() {
        // Stops timer.
        timer.cancel(); //when the game ends, the timer stops, meaning the game stops updating every 20 seconds.
        timer = null;
        start_flg = false; //changes boolean from true to false as the game is no longer running.
        timeLabel.setVisibility(View.VISIBLE);
        // Before showing startLayout, sleep 1 second.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);

        startLayout.setVisibility(View.VISIBLE); //Menu is now available
        //hides the game images.
        sprite.setVisibility(View.INVISIBLE);

        coin.setVisibility(View.INVISIBLE);
        heart.setVisibility(View.INVISIBLE);


    }

    /**
     * Used to move around the character through touch and release.
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg && isSwape) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() < screenWidth/2 && event.getY() > screenHeight/2) { //finger pressing down on screen.
//                action_flg = true;
                if(right){
                    left=false;
                    center=true;
                    right=false;
                }else if(center) {
                    left=true;
                    center=false;
                    right=false;
                }

            } else if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() > screenWidth/2 && event.getY() > screenHeight/2) { //finger raised off screen.
//                action_flg = false;
                if(left){
                    left=false;
                    center=true;
                    right=false;
                }else if(center) {
                    left=false;
                    center=false;
                    right=true;
                }

            }
        }
        return true;
    }

    /**
     * Closes application.
     *
     * @param view
     */
    public void exitGame(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask(); //closes app and removes it from recent task list.
        } else {
            finish();
        }
    }


    /**
     * difficulty of the games.
     * starts the game, therefore, main menu hides and objects are visible.
     * time is started to constantly update the game.
     *
     * @param view
     */
    //Repeat of games but changing the difficulty integer to apply different modifications to the objects.
    public void easyGame(View view) {
        tv_left.setVisibility(View.INVISIBLE);
        tv_right.setVisibility(View.INVISIBLE);
        isSwape=true;
        difficulty = 1; //game difficulty is set to 1, meaning the objects will be modified to meet the 'easy' criteria.
        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE); //hides the menu when the game starts.

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            spriteSize = sprite.getHeight(); //gets the height  of my character model.
            spriteX = sprite.getX(); //gets x coordinates of my character model.
            spriteY = sprite.getY(); //gets y coordinates of my character model.
        }

        frameWidth = initialFrameWidth;

        sprite.setX(0.0f);   //character starts bottom left.

        coin.setY(3000.0f); //starts out of game frame.
        heart.setY(3000.0f); //starts out of game frame.


        coinY = coin.getY();
        heartY = heart.getY();

        sprite.setVisibility(View.VISIBLE); //object is now visible, when the game starts

        coin.setVisibility(View.VISIBLE); //object is now visible, when the game starts
        heart.setVisibility(View.VISIBLE); //object is now visible, when the game starts

        timeCount = 0;
        score = 0;
        coinTotal=0;

        totoalTime = 0;
        dt = 0;
        spendTime=0;

        startTime = System.nanoTime();

        scoreLabel.setText("Score : 0"); //sets the score to 0 whenever the game starts.
        totalLabel.setText("Total : 0");
        timeLabel.setText("Time : 0");
        timeLabel.setVisibility(View.INVISIBLE);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }
    public void easyGame2(View view) {
        tv_left.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.VISIBLE);
        isSwape=false;
        difficulty = 1; //game difficulty is set to 1, meaning the objects will be modified to meet the 'easy' criteria.
        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE); //hides the menu when the game starts.

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            spriteSize = sprite.getHeight(); //gets the height  of my character model.
            spriteX = sprite.getX(); //gets x coordinates of my character model.
            spriteY = sprite.getY(); //gets y coordinates of my character model.
        }

        frameWidth = initialFrameWidth;

        sprite.setX(0.0f);   //character starts bottom left.

        coin.setY(3000.0f); //starts out of game frame.
        heart.setY(3000.0f); //starts out of game frame.

        totoalTime = 0;
        dt = 0;
        spendTime=0;

        startTime = System.nanoTime();
        coinY = coin.getY();
        heartY = heart.getY();


        sprite.setVisibility(View.VISIBLE); //object is now visible, when the game starts

        coin.setVisibility(View.VISIBLE); //object is now visible, when the game starts
        heart.setVisibility(View.VISIBLE); //object is now visible, when the game starts

        timeCount = 0;
        score = 0;
        coinTotal=0;

        scoreLabel.setText("Score : 0"); //sets the score to 0 whenever the game starts.
        totalLabel.setText("Total : 0");
        timeLabel.setText("Time : 0");
        timeLabel.setVisibility(View.INVISIBLE);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }
    //repeat of easy, just difficulty is set to 2.
    public void medGame(View view) {
        difficulty = 2;
        start_flg = true;
        isSwape = true;
        startLayout.setVisibility(View.INVISIBLE);

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            spriteSize = sprite.getHeight();
            spriteX = sprite.getX();
            spriteY = sprite.getY();
        }

        frameWidth = initialFrameWidth;

        sprite.setX(0.0f);
        coin.setY(3000.0f);
        heart.setY(3000.0f);

        totoalTime = 0;
        dt = 0;
        spendTime=0;

        startTime = System.nanoTime();
        coinY = coin.getY();
        heartY = heart.getY();

        sprite.setVisibility(View.VISIBLE);
        coin.setVisibility(View.VISIBLE);
        heart.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        coinTotal=0;
        scoreLabel.setText("Score : 0");
        totalLabel.setText("Total : 0");
        timeLabel.setText("Time : 0");
        timeLabel.setVisibility(View.INVISIBLE);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }

    public void medGame2(View view) {

        tv_left.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.VISIBLE);
        isSwape=false;

        difficulty = 2;
        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE);

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            spriteSize = sprite.getHeight();
            spriteX = sprite.getX();
            spriteY = sprite.getY();
        }

        frameWidth = initialFrameWidth;

        sprite.setX(0.0f);
        coin.setY(3000.0f);
        heart.setY(3000.0f);

        totoalTime = 0;
        dt = 0;
        spendTime=0;
        startTime = System.nanoTime();
        coinY = coin.getY();
        heartY = heart.getY();

        sprite.setVisibility(View.VISIBLE);
        coin.setVisibility(View.VISIBLE);
        heart.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        coinTotal=0;
        scoreLabel.setText("Score : 0");
        totalLabel.setText("Total : 0");
        timeLabel.setText("Time : 0");
        timeLabel.setVisibility(View.INVISIBLE);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }


}

