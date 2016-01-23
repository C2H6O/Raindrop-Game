package com.ak.raindrops;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private Rectangle bucket;

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;

	private Array<Rectangle> raindrops;
	private long lastDropTime;

	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	public static final int BUCKET_WIDTH = 64;
	public static final int BUCKET_HEIGHT = 64;

	public static final int RAINDROP_WIDTH = 64;
	public static final int RAINDROP_HEIGHT = 64;

	@Override
	public void create () {
		touchPos = new Vector3();
		// load the images for the droplet and the bucket, 64x64 pixels each
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);

		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = WIDTH / 2 - BUCKET_WIDTH / 2;
		bucket.y = 20;
		bucket.width = BUCKET_WIDTH;
		bucket.height = BUCKET_HEIGHT;

		raindrops = new Array<Rectangle>();
		spawnRainDrop();


	}

	private void spawnRainDrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, WIDTH - RAINDROP_WIDTH);
		raindrop.y = HEIGHT;
		raindrop.width = RAINDROP_WIDTH;
		raindrop.height = RAINDROP_HEIGHT;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		if(Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - BUCKET_WIDTH / 2;
		}

		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRainDrop();
		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + RAINDROP_HEIGHT < 0) iter.remove();
			if(raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
		}

		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();
//		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
//		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();
//
//		if(bucket.x < 0) bucket.x = 0;
//		if(bucket.x > WIDTH - BUCKET_WIDTH) bucket.x = WIDTH - BUCKET_WIDTH;



	}
}
