package com.iillyyaa2033.mud.editor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.iillyyaa2033.mud.editor.activity.EditorActivity;
import com.iillyyaa2033.mud.editor.logic.nObject;
import java.util.ArrayList;
import com.iillyyaa2033.mud.editor.logic.nRoom;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MapEditorView extends View {

	String n = "mud.editor";
	Context context;
	EditorActivity parent = null;

	private GestureDetector detector;
    private ScaleGestureDetector scaleGestureDetector;
    private float canvasSize;
    private float mScaleFactor;
	private Paint paint, rootPaint, selectionPaint;

	public ArrayList<nRoom> rooms;		// Сам массив
	private nObject stepObj;
	private int[] toAdd;
	private int[] selectionBorder;	// can be null
	private int selectedRoomId;		// can be -1
	private int selectedObjId;
	private int mode;
	private static final int FREE = 0, OBJECT_ADDING = 1, OBJECT_EDITING = 2, ROOM_EDITING = 3, PULL_NEW_ROOM = 4;
	
	public MapEditorView(Context c) {
		super(c);
		init(c);
	}

	public MapEditorView(Context context, AttributeSet ats, int defStyle) { 
	    super(context, ats, defStyle);
		init(context);
	}   

	public MapEditorView(Context context, AttributeSet attrs) {  
		super(context, attrs); 
		init(context);
	}

	void init(Context c) {
		context = c;
		canvasSize = 5000;		// Сторона квадрата
        mScaleFactor = 1f;		// Значение зума по умолчанию

		scaleGestureDetector = new ScaleGestureDetector(c, new MyScaleGestureListener());
        detector = new GestureDetector(c, new MyGestureListener());

		rootPaint = new Paint();

		paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
		paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1f);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setSubpixelText(true);
		paint.setLinearText(true);
		paint.setFilterBitmap(true);

		selectionPaint = new Paint();
		selectionPaint.setColor(Color.RED);
		selectionPaint.setStyle(Paint.Style.STROKE);
		selectionPaint.setStrokeWidth(4);

		rooms = new ArrayList<nRoom>();
		mode = FREE;
	}

	public void setSelectionToRoom(int room_id) {
		if (room_id > rooms.size()) return;

		nRoom obj = rooms.get(room_id);
		selectedRoomId = room_id;
		//	scrollTo(getDisplay().getHeight()+obj.xc, getDisplay().getWidth() + obj.yc);
		selectionBorder = new int[]{obj.x1, obj.y1,obj.x2, obj.y2};
		invalidate();
	}

	public void editObject(int obj_id) {
		setSelectionToRoom(obj_id);
		mode = OBJECT_EDITING;
		stepObj = rooms.get(obj_id);
		toAdd = new int[]{stepObj.x1,stepObj.y1,stepObj.x2,stepObj.y2};
		rooms.remove(obj_id);
		selectionBorder = null;
		invalidate();
	}

	public void removeObject(int position) {
		rooms.remove(position);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.scale(mScaleFactor, mScaleFactor);

		rootPaint.setColor(Color.WHITE);
		rootPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, canvasSize, canvasSize, rootPaint);
		canvas.drawRect(0, 0, canvasSize, canvasSize, rootPaint);

		// DRAWING GRID
		rootPaint.setColor(Color.argb(50, 0, 0, 0));
		for (int stepx = 0; stepx < 51; stepx++) {
			canvas.drawLine(stepx * 100, 0, stepx * 100, canvasSize, rootPaint);
		}

		for (int stepy = 0; stepy < 51; stepy++) {
			canvas.drawLine(0, stepy * 100, canvasSize, stepy * 100, rootPaint);
		}

		if (mode == OBJECT_ADDING || mode == OBJECT_EDITING) {
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(toAdd[0], toAdd[1], toAdd[2], toAdd[3], paint);

			rootPaint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(toAdd[0], toAdd[1], 10, rootPaint);
			canvas.drawCircle(toAdd[2], toAdd[3], 10, rootPaint);

			canvas.drawCircle(toAdd[0], toAdd[1], 10, paint);
			canvas.drawCircle(toAdd[2], toAdd[3], 10, paint);
		} 

		// DRAWING OBJECTS
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.argb(70, 0, 0, 0));
		for (nObject obj : rooms) {
			canvas.drawRect(obj.x1, obj.y1, obj.x2, obj.y2, paint);
			canvas.drawText("" + obj.id, obj.x2, obj.y1, paint);

			if (obj.id == selectedRoomId)
				canvas.drawText("Room selected", obj.x2, obj.y1 + 10, paint);
		}

		if (selectionBorder != null) {
			canvas.drawRect(selectionBorder[0], selectionBorder[1], selectionBorder[2], selectionBorder[3], selectionPaint);
			if(mode == PULL_NEW_ROOM && toAdd == null){
				canvas.drawCircle((selectionBorder[0]+selectionBorder[2])/2, selectionBorder[1] - 30,15,paint);	// top
				canvas.drawCircle((selectionBorder[0]+selectionBorder[2])/2, selectionBorder[3] + 30,15,paint);	// down
				canvas.drawCircle(selectionBorder[0] - 30, (selectionBorder[1]+selectionBorder[3])/2,15,paint);	// left
				canvas.drawCircle(selectionBorder[2] + 30, (selectionBorder[1]+selectionBorder[3])/2,15,paint);	// right
			}
		}
	}

	@Override	// Если было нажатие
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

	private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override	// Если пользователь сделал щипок 
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor=scaleGestureDetector.getScaleFactor();	//получаем значение зума относительно предыдущего состояния

            float focusX = scaleGestureDetector.getFocusX();	//получаем координаты фокальной точки - точки между пальцами
            float focusY = scaleGestureDetector.getFocusY();

            // следим чтобы канвас не уменьшили меньше половины исходного размера 
			// и не допускаем увеличения больше чем в три раза
            if (mScaleFactor * scaleFactor > 0.25 && mScaleFactor * scaleFactor < 3) {
                mScaleFactor *= scaleGestureDetector.getScaleFactor();

                int scrollX=(int)((getScrollX() + focusX) * scaleFactor - focusX);
                int scrollY=(int)((getScrollY() + focusY) * scaleFactor - focusY);
                scrollTo(scrollX, scrollY);
				invalidate();
            }
            return true;
        }
    }


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override	// При движении пальцем
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			int constant = 1;
			int point_const = 50;
			float x1 = (e2.getX() + getScrollX()) / mScaleFactor;
            float y1 = (e2.getY() + getScrollY()) / mScaleFactor;

			switch (mode) {
				case FREE:
					scrollBy((int) distanceX, (int)distanceY);
					break;
				case OBJECT_ADDING:
				case OBJECT_EDITING:
					if ((x1 > toAdd[0] - point_const && x1 < toAdd[0] + point_const) && (y1 > toAdd[1] - point_const && y1 < toAdd[1] + point_const)) {
						toAdd[0] -= distanceX / (mScaleFactor * constant);
						toAdd[1] -= distanceY / (mScaleFactor * constant);
					} else if ((x1 > toAdd[2] - point_const && x1 < toAdd[2] + point_const) && (y1 > toAdd[3] - point_const && y1 < toAdd[3] + point_const)) {
						toAdd[2] -= distanceX / (mScaleFactor * constant);
						toAdd[3] -= distanceY / (mScaleFactor * constant);
					} else {
						toAdd[0] -= distanceX / (mScaleFactor * constant);
						toAdd[2] -= distanceX / (mScaleFactor * constant);
						toAdd[1] -= distanceY / (mScaleFactor * constant);
						toAdd[3] -= distanceY / (mScaleFactor * constant);
					}
					invalidate();
					break;
				case PULL_NEW_ROOM:
					scrollBy((int) distanceX, (int)distanceY);
					break;
			}
            return true;
        }

        @Override 	// Одиночный тап
        public boolean onSingleTapConfirmed(MotionEvent event) {
			final float x = (event.getX() + getScrollX()) / mScaleFactor;
            final float y = (event.getY() + getScrollY()) / mScaleFactor;

			if (x < 0 || y < 0) return false;

			switch (mode) {
				case FREE:
					for (nObject blank : rooms) {
						if (x > blank.x1 && x < blank.x2 && y > blank.y1 && y < blank.y2) {
							setSelectionToRoom(blank.id);
							return true;
						}
					}
					if(selectionBorder != null){
						selectionBorder = null;
						selectedRoomId = -1;
					} else {
						mode = OBJECT_ADDING;
						toAdd = new int[]{(int) x - 30,(int) y - 30,(int) x + 30,(int) y + 30};
					}
					invalidate();
					break;
				case OBJECT_ADDING:
					rooms.add(new nRoom(rooms.size(), toAdd, null, null));
					mode = FREE;
					invalidate();
					break;
				case OBJECT_EDITING:
					stepObj.setCoords(toAdd);
					rooms.add(new nRoom(stepObj, null));
					mode = FREE;
					break;
				case PULL_NEW_ROOM:
					mode = FREE;
					invalidate();
					break;
				default:
					// do nothing
			}
			return true;
        }

		@Override
		public void onLongPress(MotionEvent event) {

			switch (mode) {
				case FREE:
					(new AlertDialog.Builder(context))
						.setTitle("Obj id is " + selectedRoomId)
						.setItems(new String[]{"Edit this room","Pull new room","Clear selection"}, new AlertDialog.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2) {
								switch (p2) {
									case 1:
										mode = PULL_NEW_ROOM;
										toAdd = null;
										invalidate();
										break;
									case 2:
										selectedRoomId = -1;
										selectionBorder = null;
										invalidate();
										break;
								}
							}
						})
						.show();
			}
		}
    }
}
