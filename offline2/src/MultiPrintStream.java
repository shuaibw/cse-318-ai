import java.io.OutputStream;
import java.io.PrintStream;

public class MultiPrintStream extends PrintStream {
    private final PrintStream[] streams;

    public MultiPrintStream(PrintStream... streams) {
        super(new OutputStream() {
            @Override
            public void write(int b) {
                for (PrintStream stream : streams) {
                    stream.write(b);
                }
            }
        });
        this.streams = streams;
    }

    @Override
    public void flush() {
        super.flush();
        for (PrintStream stream : streams) {
            stream.flush();
        }
    }

    @Override
    public void close() {
        super.close();
        for (PrintStream stream : streams) {
            stream.close();
        }
    }
}