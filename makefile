# Author: Jake Wachs
# CS 403, Programming Languages
# The University of Alabama
#
# Parent Makefile
# Does the main rule need to be 'make parser', 'make recognizer',
#	or does it not matter because I'll have a shell script for executing?

# cat the source code file before running
# make sure to have ./ before executable

OPTS = -Xlint
CLASSES = Types.class Lexeme.class Lexer.class Recognizer.class Parser.class

recognizer: $(CLASSES)

run: $(CLASSES)
	make test1
	make test2
	make test3
	make test4
	make test5

##### Classes #####
Types.class: lex/Types.java
	javac $(OPTS) lex/Types.java

Lexeme.class: lex/Lexeme.java
	javac $(OPTS) lex/Lexeme.java

Lexer.class: lex/Lexer.java
	javac $(OPTS) lex/Lexer.java

Recognizer.class: parse/Recognizer.java
	javac $(OPTS) parse/Recognizer.java

Parser.class: parse/Parser.java
	javac $(OPTS) parse/Parser.java

##### Test Cases #####
test1:
	@echo "Running test1, should fail"
	@cat parse/test1.flex
	-./recognizer parse/test1.flex
	@echo "\n=========================\n"

test2:
	@echo "Running test2, should pass"
	@cat parse/test2.flex
	-./recognizer parse/test2.flex
	@echo "\n=========================\n"

test3:
	@echo "Running test3, should fail"
	@cat parse/test3.flex
	-./recognizer parse/test3.flex
	@echo "\n=========================\n"

test4:
	@echo "Running test4, should pass"
	@cat parse/test4.flex
	-./recognizer parse/test4.flex
	@echo "\n=========================\n"

test5:
	@echo "Running test5, should fail"
	@cat parse/test5.flex
	-./recognizer parse/test5.flex
	@echo "\n"

##### Clean Command #####
clean:
	rm *.class
