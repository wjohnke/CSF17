{-# OPTIONS_GHC -fno-warn-tabs #-}
{-# LANGUAGE DeriveFoldable #-}
{-# LANGUAGE DeriveFunctor #-}
{-# LANGUAGE DeriveTraversable #-}
{-# LANGUAGE FlexibleInstances #-}
{-# LANGUAGE GeneralizedNewtypeDeriving #-}
{-# LANGUAGE LambdaCase #-}

module Parser where

import Data.Char
import Control.Monad
import Control.Applicative (Alternative, empty, (<|>), (<*>), (<$>))

newtype Parser a = P (String -> [(a,String)])

parse :: Parser a -> String -> [(a,String)]
parse (P p) inp  = p inp

instance Functor Parser where
  fmap f (P p) = P (\s -> [(f x,y)|(x,y) <- p s])

instance Monad Parser where
   return v = P (\s -> [(v, s)])
   p >>= f  = P (\s -> concat [parse (f a) s' | (a, s') <- parse p s])


instance Applicative Parser where
  pure             = return
  (P p) <*> (P q)  = P (\s -> [(f i, y)| (f, x) <- p s, (i, y) <- q x])

instance Alternative Parser where
  empty   = P (\s -> [])
  p <|> q = P (\s -> case parse (mplus p q) s of
                       [] -> []
                       (res:_) -> [res])

instance MonadPlus Parser where
   mzero     = P (\s -> [])
   mplus p q = P (\s -> case parse p s of
                          []  ->  parse q s
                          res ->  res)

infixr 5 +++
(+++)                         :: Parser a -> Parser a -> Parser a
p +++ q                       =  p `mplus` q

failure :: Parser a
failure =  mzero

item :: Parser Char
item =  P (\case
             []     -> []
             (x:xs) -> [(x,xs)])

satisfy   :: (Char -> Bool) -> Parser Char
satisfy p = do i <- item
               if p i then return i else failure

oneOf     :: String -> Parser Char
oneOf ps  =  satisfy (flip elem ps)

digit     :: Parser Char
digit     =  satisfy isDigit

lower     :: Parser Char
lower     =  satisfy isLower

upper     :: Parser Char
upper     =  satisfy isUpper

letter    :: Parser Char
letter    =  satisfy isAlpha

alphanum  :: Parser Char
alphanum  =  satisfy isAlphaNum

char      :: Char -> Parser Char
char x    =  satisfy (x ==)

string        :: String -> Parser String
string []     =  return []
string (x:xs) =  do { char x
                    ; string xs
                    ; return (x:xs)
                    }
  
some          :: Parser a -> Parser [a]
some p        = some_p
                where many_p = some_p <|> pure []
                      some_p = (:) <$> p <*> many_p

many          :: Parser a -> Parser [a]
many p        =  many1 p <|> return []

many1         :: Parser a -> Parser [a]
many1 p       =  do v  <- p
                    vs <- many p
                    return (v:vs)

ident         :: Parser String
ident         =  do x  <- lower +++ upper
                    xs <- many alphanum
                    return (x:xs)

nat           :: Parser Int
nat           =  do xs <- many1 digit
                    return (read xs)

int              :: Parser Int
int                 =  do char '-'
                          n <- nat
                          return (-n)
                       <|> nat

space               :: Parser ()
space               =  do many (satisfy isSpace)
                          return ()

spaces              :: Parser String
spaces              =  many $ oneOf " \n\r"

token'             :: Parser a -> Parser a
token' p           =  do space
                         v <- p
                         space
                         return v

token              :: Parser a -> Parser a
token p            =  do v <- p
                         spaces
                         return v

identifier         :: Parser String
identifier         =  token ident <|> token' ident

natural            :: Parser Int
natural            =  token nat

integer            :: Parser Int
integer            =  token int

symbol             :: String -> Parser String
symbol xs          =  token' (string xs)

reserved           :: String -> Parser String
reserved xs        =  token (string xs)

comma              :: Parser String
comma              = symbol ","

between           :: Parser a -> Parser b -> Parser String -> Parser String
between op cl p   = do op
                       x <- p
                       cl
                       return x

parens         :: Parser a -> Parser a
parens p       = do symbol "("
                    x <- p
                    symbol ")"
                    return x

braces         :: Parser a -> Parser a
braces p       = do symbol "{"
                    x <- p
                    symbol "}"
                    return x

corners        :: Parser a -> Parser a
corners p      = do symbol "<"
                    x <- p
                    symbol ">"
                    return x

sepBy           :: Parser a -> Parser b -> Parser [a]
sepBy p sep     = sepBy1 p sep <|> return []

sepBy1          :: Parser a -> Parser b -> Parser [a]
sepBy1 p sep    = do x <- p
                     xs <- many (sep >> p)
                     return (x:xs)

sepEndBy        :: Parser a -> Parser b -> Parser [a]
sepEndBy p sep  = sepEndBy1 p sep <|> return []

sepEndBy1       :: Parser a -> Parser b -> Parser [a]
sepEndBy1 p sep = do { x <- p
                     ; do { sep
                          ; xs <- sepEndBy p sep
                          ;  return (x:xs)
                          }
                       <|> return [x]
                     }

endBy           :: Parser a -> Parser b -> Parser [a]
endBy p sep     = many (do { x <- p ; sep ; return x})

endBy1          :: Parser a -> Parser b -> Parser [a]
endBy1 p sep    = many1 (do { x <- p ; sep ; return x})

optionMaybe     :: Parser a -> Parser (Maybe a)
optionMaybe p   = P (\s -> case parse p s of
                             []         -> [(Nothing, s)]
                             [(v, out)] -> [(Just v, out)])


chainl :: Parser a -> Parser (a -> a -> a) -> a -> Parser a
chainl p op a = (p `chainl1` op) <|> return a

chainl1 :: Parser a -> Parser (a -> a -> a) -> Parser a
chainl1 p op  = do { a <- p
                   ; rest a
                   }
  where rest a = (do f <- op
                     b <- p
                     rest (f a b))
                 <|> return a

chainr :: Parser a -> Parser (a -> a -> a) -> a -> Parser a
chainr p op a = (p `chainr1` op) <|> return a

chainr1 :: Parser a -> Parser (a -> a -> a) -> Parser a
chainr1 p op   = go
  where go     = do { a <- p
                    ; rest a
                    }
        rest a = (do f <- op
                     b <- go
                     rest (f a b))
                 <|> return a



