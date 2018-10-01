{-# OPTIONS_GHC -fwarn-missing-signatures #-}
{-# OPTIONS_GHC -fno-warn-tabs #-}
module Homework3 where

import Test.Hspec
import RPNAST
{-
******
Will Johnke
ID: 14253530
CS 4450
Homework #3
******
-}


{-
Prob1
This function takes in a string of potential Ops, separated by whitespace,
and parses it into a structured PExp, or a list of Ops
-}

prob1    :: String -> PExp
prob1 "" = []
prob1 a = parseString (words a)


parseString :: [String] -> [Op]
parseString [] = []
parseString (x:xs)
					 | x=="+"  = Plus : parseString xs
					 | x=="-"  = Minus : parseString xs
					 | x=="*"  = Mul : parseString xs
					 | x=="/" = IntDiv : parseString xs
					 | x==""   = []
					 | otherwise = Val (read x) : parseString xs
{------------------------------------------------------------------------------------------------}	
{-
Prob2
This function utilizes a stack to calculate a given Reverse Polish Notation equation,
pulling off the stack each time an operator is encountered, and evaluating the value
within the pop function, pushing the returned value back on the stack to ensure
structure of the RPN, and continuing until end of input, with minor error handling

The push/pop functions were mostly obtained from the examples done in class, although
I modified the push function to take in a tuple as well.
-}
prob2    :: PExp -> Int
prob2 [] = 0
prob2 [x] = errorWithoutStackTrace "Bad input"
prob2 [x, y] = errorWithoutStackTrace "Bad input"
prob2 ops = calculateExpression ops []

push :: (Int, [Int]) -> [Int]
push (x, []) = [x]
push (x, stack) = (x:stack)

pop :: [Int] -> Op -> (Int, [Int]) --Popping two values off stack at once & performing calculation
pop [] _ = errorWithoutStackTrace "Bad input"
pop [x] _ = errorWithoutStackTrace "Bad input"
pop (x:y:xs) op   | op==Plus   = (x+y,xs)
				  | op==Minus  = (y-x,xs)
				  | op==Mul    = (x*y,xs)
				  | op==IntDiv = (y `div` x, xs)
				  | otherwise = errorWithoutStackTrace "Bad Input"

getVal :: Op -> Int
getVal (Val x) = x

calculateExpression :: PExp -> [Int] -> Int
calculateExpression [] stack =  if (stack==[] || (length stack)>1)
								then errorWithoutStackTrace "Bad input"
								else head stack
calculateExpression (x:xs) stack 
								| x==Plus   =  calculateExpression xs (push (pop stack x))
								| x==Minus  =  calculateExpression xs (push (pop stack x))
								| x==Mul    =  calculateExpression xs (push (pop stack x))
								| x==IntDiv =  calculateExpression xs (push (pop stack x))
								| otherwise =  calculateExpression xs (push ((getVal x), stack) )
{------------------------------------------------------------------------------------------------}	
{-
Prob3
This function extends the calculations performed in Prob2, using the Result monad to help handle errors.
There is also a few differences in how the calculation of operands is performed, and the code structure
is modified to allow for error handling as well
-}

										
prob3    :: PExp -> RPNResult
prob3    [] = Failure BadSyntax
prob3 	[x] = Failure BadSyntax
prob3  [x, y]= Failure BadSyntax
prob3 ops= calculateExpression3 ops []

push3 :: (Int, [Int]) -> [Int]
push3 (x, []) = [x]
push3 (x, stack) = (x:stack)

pop3 :: [Int] -> (Int, [Int])
pop3 [] = (-1, [])
pop3 [x] = (x, [])
pop3 (x:xs) =  (x, xs)

calculateExpression3 :: PExp -> [Int] -> RPNResult
calculateExpression3 [] stack =  if (stack==[] || (length stack)>1)
								then Failure BadSyntax
								else Success (head stack)
								
calculateExpression3 (x:xs) stack |(x==Plus) =
									do{
										let (x1,new)=pop3 stack
										;if((length new)==0) then Failure BadSyntax
										;else let(x2,newStack)=pop3 new in calculateExpression3 xs (push3 ((x1+x2), newStack))
									}
								  |x==Minus =
									do{
										let (x1,new)=pop3 stack
										;if((length new)==0) then Failure BadSyntax
										;else let(x2,newStack)=pop3 new in calculateExpression3 xs (push3 ((x2-x1), newStack))
									}
								  |x==Mul =
									do{
										let (x1,new)=pop3 stack
										;if((length new)==0) then Failure BadSyntax
										;else let(x2,newStack)=pop3 new in calculateExpression3 xs (push3 ((x1*x2), newStack))
									}
								  |x==IntDiv =
									do{
										let (x1,new)=pop3 stack
										;if((length new)==0) then Failure BadSyntax
										;else let(x2, newStack)= pop3 new in if (x1/=0) then calculateExpression3 xs (push3 ((x2 `div` x1), newStack) )
											else Failure DivByZero
									}
								  |	otherwise = (calculateExpression3 xs (push3 ((getVal x), stack)) )
{-------------------------------------------------------------------------------------------------------}
{-
Prob4 
This function parses a PExp as input, returning either Result with a Failure and a String message, or Success
and a String message (the syntactically correct output). It processes the ops it is given and prints out the
string infix representation of the calculation, failing when a calculation is invalid or syntax is incorrect.
-}

prob4    :: PExp -> Result String String
prob4 []  = Failure "Bad Syntax"
prob4 [x, y]=Failure "BadSyntax"
prob4 xs= parseInput xs []


push4 :: (String, [String]) -> [String]
push4 (x, []) = [x]
push4 (x, stack) = (x:stack)

pop4 :: [String] -> (String, [String])
pop4 [] = ("", [])
pop4 [x] = (x, [])
pop4 (x:xs) =  (x, xs)

parseInput :: PExp -> [String] -> Result String String
parseInput [] stack =   if (stack==[] || (length stack)>1)
							then Failure "Bad Syntax"
							else Success (head(stack))

parseInput (x:xs) stack  	  | x==Plus =
									do{
										let (newVal, new)=pop4 stack
										;if(length(new)==0) then Failure "Bad Syntax"
										else let(oldExp,newStack)=pop4 new in parseInput xs (push4 ("("++oldExp++" + "++newVal++") ", newStack)) 
									}
							  | x==Minus =
									do{
										let (newVal, new)=pop4 stack
										;if(length(new)==0) then Failure "Bad Syntax"
										else let(oldExp,newStack)=pop4 new in parseInput xs (push4 ("("++oldExp++" - "++newVal++") ", newStack)) 
									}
							  | x==Mul =
									do{
										let (newVal, new)=pop4 stack
										;if(length(new)==0) then Failure "Bad Syntax"
										else let(oldExp,newStack)=pop4 new in parseInput xs (push4 ("("++oldExp++" * "++newVal++") ", newStack)) 
									}
							  | x==IntDiv =
									do{
										let (newVal, new)=pop4 stack
										;if(length(new)==0) then Failure "Bad Syntax"
										else let(oldExp,newStack)=pop4 new in parseInput xs (push4 ("("++oldExp++" / "++newVal++") ", newStack)) 
									} 
							   | otherwise = parseInput xs (push4 ( (show(getVal(x))),stack))
{----------------------------------------------------------------------------------------------------------------------------------------------}

-- Write your Hspec Tests below

