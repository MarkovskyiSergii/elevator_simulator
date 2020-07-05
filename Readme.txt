Author: Markovskyi Serhii

In test case used:
- SpringFramework v. 2.3.1
- Maven 
- Java  v. 11

Foreign library
- Lombok ( https://projectlombok.org/features/all )

Example of console output

	*** STEP 1 ***
1.  2.  3.   4.        5. 6.
6)	1  |			| [9][UP]
5)	1  |			| [3][DOWN]
4)	3  |			| [15, 16, 15][UP]
3)	3  |			| [2, 16, 10][DOWN,UP]
2)	1  |3.	 4.		| [16][UP]
1)	1  |N [EMPTY]N	| [3][UP]

1. Number of floor
2. Quantity passengers at floor
3. Direction of Elevator
   N - direction not setup (NOT_CHOSEN)
   ^ - direction UP
   v - direction DOWN
4. The Elevator
   [EMPTY] - all passengers out or this is start simulation
   [2,5,4] - queue of passengers if Elevator go UP
   [9,5,3] - if Elevator go DOWN
5. Passengers at floor. They loading in Elevator only with ordering by Direction.
   [2,16,10] - described as selected floor
6. Direction at floor chosen passengers
   [DOWN,UP] - Elevator given passengers only with the same directional