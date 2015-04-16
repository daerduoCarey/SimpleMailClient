all:
	mkdir bin
	javac -cp javabase64-1.3.1.jar:src src/mail/*.java -d bin/
	
run:
	java -cp javabase64-1.3.1.jar:bin mail/Main

clear:
	rm -rf bin
