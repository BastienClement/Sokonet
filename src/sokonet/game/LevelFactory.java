package sokonet.game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

/**
 *
 */
public abstract class LevelFactory {
	/**
	 *
	 */
	private static class LevelBlueprintBuilder {
		static Collector<String, LevelBlueprintBuilder, List<LevelBlueprint>> collector = Collector.of(
			LevelBlueprintBuilder::new,
			LevelBlueprintBuilder::accept,
			LevelBlueprintBuilder::merge,
			LevelBlueprintBuilder::finish
		);

		private List<LevelBlueprint> blueprints = new ArrayList<>();
		private List<String> current = new ArrayList<>();

		/**
		 *
		 * @param line
		 */
		void accept(String line) {
			if (line.trim().isEmpty()) {
				commit();
			} else {
				current.add(line);
			}
		}

		/**
		 *
		 */
		void commit() {
			if (current.isEmpty()) return;
			blueprints.add(new LevelBlueprint(current.toArray(new String[0])));
			current.clear();
		}

		/**
		 *
		 * @param other
		 * @return
		 */
		LevelBlueprintBuilder merge(LevelBlueprintBuilder other) {
			throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return
		 */
		List<LevelBlueprint> finish() {
			commit();
			return blueprints;
		}
	}

	/**
	 *
	 */
	private static class LevelBlueprint {
		int width;
		int height;
		Point player;
		List<Point> walls = new ArrayList<>();
		List<Point> crates = new ArrayList<>();
		List<Point> targets = new ArrayList<>();

		/**
		 *
		 * @param lines
		 */
		LevelBlueprint(String[] lines) {
			width = 0;
			height = lines.length;

			for (int y = 0; y < height; y++) {
				byte[] line = lines[y].getBytes(StandardCharsets.US_ASCII);
				for (int x = 0; x < line.length; x++) {
					byte c = line[x];
					if (c == ' ') continue;
					if (x >= width) width = x + 1;

					Point pt = new Point(x, y);
					switch (c) {
						case '@':
							if (player != null) throw new IllegalStateException();
							player = pt;
							break;

						case '#':
							walls.add(pt);
							break;

						case '$':
							crates.add(pt);
							break;

						case '.':
							targets.add(pt);
							break;

						case '*':
							crates.add(pt);
							targets.add(pt);
							break;

						default:
							throw new IllegalArgumentException("Unknown item: " + c);
					}
				}
			}

			if (width < 1 || height < 1) throw new IllegalStateException();
		}

		/**
		 *
		 * @param index
		 * @return
		 */
		Level build(int index) {
			Level level = new Level(index, width, height);
			crates.forEach(level::setCrate);
			walls.forEach(level::setWall);
			targets.forEach(level::setTarget);
			level.setPlayer(player);
			level.scanOutsides();
			return level;
		}
	}

	private static List<LevelBlueprint> blueprints = new ArrayList<>();

	/**
	 * @param file
	 * @throws IOException
	 */
	public static void load(String file) throws IOException {
		blueprints.addAll(Files.lines(Paths.get(file))
		                       .filter(line -> !line.trim().startsWith(";"))
		                       .collect(LevelBlueprintBuilder.collector));
	}

	/**
	 * @return
	 */
	public static int getNumLevels() {
		return blueprints.size();
	}

	/**
	 * @param index
	 * @return
	 */
	public static Level getLevel(int index) {
		return blueprints.get(index).build(index);
	}

	/**
	 *
	 */
	private LevelFactory() {}
}
