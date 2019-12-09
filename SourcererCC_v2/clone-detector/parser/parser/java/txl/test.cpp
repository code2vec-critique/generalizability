struct A : public B
{
    A() try : B(), foo(1), bar(2)
    {
        // constructor body 
    }
    catch (...)
    {
        // exceptions from the initializer list are caught here
        // but also rethrown after this block (unless the program is aborted)
    }
 
private:
    Foo foo;
    Bar bar;
};

int myFunction(int a) {
	return a;
}
