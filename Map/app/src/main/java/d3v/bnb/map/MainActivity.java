package d3v.bnb.map;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView mapImage;
    ImageView cursorImage;
    final static int ORIGIN_X = 45;
    final static int ORIGIN_Y = 806;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapImage = (ImageView) findViewById(R.id.map);
        cursorImage = (ImageView) findViewById(R.id.cursor);

        //TODO: OnTouchListener for testing points only. Can be deleted.
        mapImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float x = event.getX();
                float y = event.getY();

                System.out.println("X: " + x +"\nY: " + y);

                return false;
            }
        });

        cursorImage.setVisibility(View.VISIBLE);
        cursorImage.setX(ORIGIN_X);
        cursorImage.setY(ORIGIN_Y);

        //TODO: Method called inside OnCreate for test purpose only.
        drawCurrentPosition(1f,1f);
    }

    //Method to Draw the current position
    private void drawCurrentPosition(float realX, float realY){

        float newPosX = realY*55;
        float newPosY = realX*55;

        cursorImage.setX(ORIGIN_X + newPosX);
        cursorImage.setY(ORIGIN_Y - newPosY);
    }
}
