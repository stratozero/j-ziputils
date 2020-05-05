package it.mormao.zip;

import java.io.IOException;

public interface ThrowingIOBiConsumer<T, U>{
	void accept(T t, U u) throws IOException;
}
