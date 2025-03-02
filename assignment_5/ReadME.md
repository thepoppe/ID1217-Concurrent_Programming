# Homework 5 in ID1217 Concurrent programming

## Task Determine all common values in three arrays. A.k.a. The Welfare Crook problem (by Prof. D. Gries) (40 points)
Assume there are three distributed processes: F, G, and H, each having a local array of strings (or integers): f[1:n], g[1:n], and h[1:n], respectively. Assume that the first array f[1:n] is a list that contains names (or personal numbers) of people working at IBM Yorktown,  the second g[1:n] is a list of students at Columbia, and the third h[1:n] is a list of people on welfare in New York City. Assume that there is at least one person whose name is on all three lists. [ One can think that something peculiar is going on if the person's name is on all three lists! ].

Develop and implement a distributed program in which all three processes interact with each other until each process has determined all names, each of which is on all three lists. Each process prints the common names that it has determined. Use message passing (or RMI) for interaction between the processes; do not use shared variables. Messages may contain only one value at a time and (if needed) process ids. Do not send entire arrays in messages (or RMI calls) because the arrays can be huge. You can implement your distributed application in C using the RMI library or in Java using the socket API or Java RMI. In comments in the program or a README file, explain your solution (algorithm) shortly. 

## Original
This structure is built upon steps from the lecture slide
Typical development and execution steps
1. Define a remote interface(s) that extends java.rmi.Remote.
2. Develop a class (a.k.a. servant class) that implements the
interface.
3. Develop a server class that provides a container for servants, i.e.,
creates the servants and registers them at the Naming Service.
4. Develop a client class that gets a reference to a remote object(s)
and calls its remote methods.
5. Compile all classes and interfaces using javac.
6. Start the Naming service rmiregistry
7. Start the server on a server host, and run the client on a client host.

### Classes
- NameGenerator.java: Util class to generate random names for the Processes.
- NameCompInterface.java: The interface for RMI-protocol communication
    - boolean checkName(String name): compares the given name to the local list
    - String getName(int id): Recieves the name of the index in the list
    - void notifyCommon(String name): Used to notify a match to others
    - void printCommon(): used to inform the evant to print the results
- NameComp.java: The RMI servant, provides implementations for the interface
- Server.java: Creates and registers the servants in the registry
- Client.java: Simulates one of the processes
    - Depedning on args when requested a different process is started
    - Then starts the comparisons between the processes

### Algorithm for finding matches
1. Continuous Iteration:
    Loop indefinitely to process names from the local list.
2. Retrieve the Next Name: 
    Attempt to obtain the next name from the local array. An exception indicates that all names have been checked, exit the loop.
3. Skip Already Processed Names:
    If the retrieved name is null (indicating it has been handled already), skip to the next iteration.
4. Send for Verification:
    Transmit the current name as a single-value message to each remote process and await their Boolean responses.
5. Common Name Confirmation:
    If every remote process confirms the presence of the name (all responses are true), then the name is common to all processes.
6. Notify Common name:
    Notify all process that a match was found. Each process double-checks that its not already in their common list before adding.
7. Print the Results:
    Once the loop exits (i.e., all names have been processed), instruct all processes to print their common names.

### Discussion
This method registers all three clients on a single server and the client class is used to
simulated a distributed system. Messages are being sent between the servant functions. 
While this isnt truly showing without an extensive print trace.

The algorithm itself is simple: it sends each piece of information (a name) to all processes and waits for responses. 
This “fully connected” approach guarantees that all processes reaches the same final list of common names. While the original
solution only iterates over 1 of the array it finds all the common matches and informs the others to update their common
list as well. With all three processess running simultaneously other would contribute as well.

One potential improvement is to replace the getName(int id) method with a getNextName() method. With this change, 
the remote object would internally maintain the pointer to the next unprocessed name and handle the iteration. 
This would simplify client code and encapsulate the iteration logic within the remote object itself.


## Alternative
The alternative solution provided was an attempt to force the three processes to be up
simultaneously. In this code the serverant class registers itself on the rmi and a child
class invokes the main function to start the code for each Process.

### Classes
- NameProcess.java: This is the servant class implementing the interface protocol.
    It also registers itself on the RMI registry and provides methods to invoke for
    the child class.
    - Very similar implementation of the protocol methods as NameComp.java
    - Additional function for the child class with a distinct Process name to use.
    - collectOtherRemotes(): performs a busy wait until all three processes are registered which
        is not an ideal solution but works to enforce a wait to see the three processes work simulataneous.
    - findCommonNames(): is the main worker which performs the same algorithm as Client.java
    - waitForOthers(): Also uses a busy wait to wait for the other processes to complete
- ProcessF, ProcessG, ProcessH: enherits from the NameProcess.java and provides the Main function to start
    the Process. ProcessF has additional responsible to create the registry while the other just collects it.
    Not ideal but I had troubles starting the rmi registry otherwise.
    - Main(): Creates the Process, and starts the search, waits for completion, prints the results and exits.
- original.NameCompInterface: isDone() added for the busy wait in waitForOthers()


### Discusssion
This implementation gave an impression of multiple processes cooperating since each process register itself
to the registry. This implementation was mainly done in curiosity to learn more how the RMI could be used. 
There are a lot of things happening under the hood with RMI and it would be interesting to see a more refined
implementation with real servers communication with eachother.
