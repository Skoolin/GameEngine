package particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Light;
import toolbox.InsertionSort;

public class ParticleMaster {
	
	private static Map<ParticleSystem, List<Particle>> particles = new HashMap<ParticleSystem, List<Particle>>();
	private static List<ParticleSystem> systems = new ArrayList<ParticleSystem>();
	private static ParticleRenderer renderer;
	
	public static void init(Matrix4f projectionMatrix) {
		renderer = new ParticleRenderer(projectionMatrix);
	}
	
	public static void update(Camera camera) {
		Iterator<Entry<ParticleSystem, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while(mapIterator.hasNext()) {
			Entry<ParticleSystem, List<Particle>> pair = mapIterator.next();
			ParticleSystem system = pair.getKey();
			List<Particle> batch =pair.getValue();
			Iterator<Particle> iterator = batch.iterator();
			while(iterator.hasNext()) {
				Particle p = iterator.next();
				boolean stillAlive = p.update(camera);
				if(!stillAlive) {
					iterator.remove();
					if(batch.isEmpty()) {
						mapIterator.remove();
						systems.remove(system);
					}
				}
			}
			InsertionSort.sortHighToLow(batch);
			system.update(camera);	
		}
		InsertionSort.sortHighToLowS(systems);
	}
	
	public static void renderParticles(Camera camera, List<Light> lights) {
		renderer.render(particles, lights, systems, camera);
	}

	public static void cleanUp() {
		renderer.cleanUp();
	}
	
	public static void addParticle(Particle particle, ParticleSystem system) {
		List<Particle> batch = particles.get(system);
		if(batch == null) {
			batch = new ArrayList<Particle>();
			particles.put(system, batch);
			systems.add(system);
		}
		batch.add(particle);
	}
}
