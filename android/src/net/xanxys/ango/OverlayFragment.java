package net.xanxys.ango;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import rajawali.Object3D;
import rajawali.RajawaliFragment;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.math.vector.Vector3;
import rajawali.primitives.Cube;
import rajawali.renderer.RajawaliRenderer;
import rajawali.scene.RajawaliScene;

public class OverlayFragment extends RajawaliFragment {
	private static final String TAG = "OverlayFragment";

	protected OverlayRenderer renderer;
	protected boolean roomUpdated = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setGLBackgroundTransparent(true);
		
		renderer = new OverlayRenderer(getActivity());
		renderer.setSurfaceView(mSurfaceView);
		setRenderer(renderer);
	}

	@Override
	public void onDestroy() {
		// Squash error from RajawaliFragment.
		try {
			super.onDestroy();
		} catch (NullPointerException e) {
			Log.i(TAG, "Squashed error", e);
		}
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return mSurfaceView;
	}

	private final class OverlayRenderer extends RajawaliRenderer {
		private DirectionalLight mLight;

		private Object3D tiles;

		private double rot_angle = 0;

		public OverlayRenderer(Context context) {
			super(context);
			setFrameRate(30);
		}

		@Override
		protected void initScene() {
			// Set background to transparent.
			getCurrentScene().setBackgroundColor(0);
			
			mLight = new DirectionalLight(0, -1, 0);
			mLight.setColor(1.0f, 1.0f, 1.0f);
			mLight.setPower(1);

			getCurrentScene().addLight(mLight);

			this.tiles = getTiles();
			getCurrentScene().addChild(tiles);

			// Set intrinsic parameters.
			getCurrentCamera().setNearPlane(0.05);
			getCurrentCamera().setFarPlane(50);
			getCurrentCamera().setFieldOfView(80);
		}

		/**
		 * Generate enough tiles to cover current point cloud. This generates
		 * some tiles even the point cloud is empty.
		 * 
		 * @return {@link Object3D} with bunch of tiles as children.
		 */
		private Object3D getTiles() {
			final int x0 = -2;
			final int x1 = 2;
			final int y0 = -2;
			final int y1 = 2;

			// Create tiles.
			Object3D tiles = new Object3D();
			Material tile_material = new Material();
			tile_material.setColor(0x888888);
			for (int ix = x0; ix <= x1; ix++) {
				for (int iy = y0; iy <= y1; iy++) {
					Object3D tile = new Cube(1);
					tile.setMaterial(tile_material);
					tile.setScale(0.95, 0.01, 0.95);
					tile.setPosition(ix, 0, iy);
					tiles.addChild(tile);
				}
			}
			return tiles;
		}

		/**
		 * TODO: Extract {@link Camera} parameters
		 */
		@Override
		public void onDrawFrame(GL10 glUnused) {
			final RajawaliScene scene = getCurrentScene();
			final double radius = 3;
			rot_angle += 0.01;

			getCurrentCamera().setPosition(
					new Vector3(Math.sin(rot_angle) * radius, 1, Math
							.cos(rot_angle) * radius));
			getCurrentCamera().setUpAxis(new Vector3(0, 1, 0));
			getCurrentCamera().setLookAt(new Vector3(0, 1, 0));

			// Draw.
			super.onDrawFrame(glUnused);
		}

	}
}
