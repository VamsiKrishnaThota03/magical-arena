# Build, run and test Magical Arena with nothing but the JDK.
SRC      := $(shell find src/main/java -name '*.java')
TEST_SRC := $(shell find src/test/java -name '*.java')

.PHONY: build run test clean

build:
	javac -d out/main $(SRC)

run: build
	java -cp out/main com.magicalarena.MagicalArena $(ARGS)

test: build
	javac -d out/test -cp out/main $(TEST_SRC)
	java -cp out/main:out/test com.magicalarena.MagicalArenaTests

clean:
	rm -rf out
