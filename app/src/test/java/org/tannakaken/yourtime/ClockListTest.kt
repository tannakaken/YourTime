package org.tannakaken.yourtime

import android.test.mock.MockContext
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito.mock
import java.io.*

/**
 * [ClockList]をテスト
 * Created by kensaku on 2017/06/20.
 */
class ClockListTest {
    @Test
    fun 最初は0が選択されている() {
        assertEquals(0, ClockList.currentClockIndex)
    }

    @Test
    fun ファイルが存在しないときのinitをテストする() {
        val tMockContext = object : MockContext() {
            override fun getFileStreamPath(name: String?): File {
                return object : File("") {
                    override fun exists(): Boolean {
                        return false
                    }
                }
            }
        }
        ClockList.init(tMockContext)
        assertEquals("私の時間",ClockList[0].name)
        assertEquals("24時間時計", ClockList[1].name)
        assertEquals("仏革命暦十進化時間", ClockList[2].name)

    }

    @Test
    fun ファイルが存在するときのinitをテストする() {
        val tMockContext = object : MockContext() {
            override fun getFileStreamPath(name: String?): File {
                return object : File("") {
                    override fun exists(): Boolean {
                        return true
                    }
                }

            }

            override fun openFileInput(name: String?): FileInputStream {
                return object : FileInputStream(FileDescriptor()) {
                    private val data = "その１\t2\t12\t60\t60\ttrue\ttrue\nその２\t3\t10\t100\t100\ttrue\ttrue\nその３\t1\t24\t60\t60\ttrue\tfalse\n"
                    private val stream = ByteArrayInputStream(data.toByteArray())
                    override fun available(): Int {
                        return stream.available()
                    }

                    override fun close() {
                        stream.close()
                    }

                    override fun mark(readlimit: Int) {
                        stream.mark(readlimit)
                    }

                    override fun markSupported(): Boolean {
                        return stream.markSupported()
                    }

                    override fun read(): Int {
                        return stream.read()
                    }

                    override fun read(b: ByteArray?): Int {
                        return stream.read(b)
                    }

                    override fun read(b: ByteArray?, off: Int, len: Int): Int {
                        return stream.read(b, off, len)
                    }

                    override fun reset() {
                        stream.reset()
                    }

                    override fun skip(n: Long): Long {
                        return stream.skip(n)
                    }
                }
            }
        }
        ClockList.init(tMockContext)
        assertEquals("その１",ClockList[0].name)
        assertEquals(MyClock.Ampm.AMPM, ClockList[0].ampm)
        assertEquals(12, ClockList[0].hours)
        assertEquals(100, ClockList[1].minutes)
        assertEquals(100, ClockList[1].seconds)
        assertEquals(true, ClockList[2].dialFromOne)
        assertEquals(false, ClockList[2].clockwise)

    }

    @Test
    fun saveをテストする() {
        val stream = ByteArrayOutputStream()
        val tMockContext = object : MockContext() {
            override fun openFileOutput(name: String?, mode: Int): FileOutputStream {
                return object : FileOutputStream(FileDescriptor()) {
                    override fun write(b: ByteArray?) {
                        stream.write(b)
                    }
                }
            }
        }
        ClockList.clear()
        ClockList.add(MyClock("その１",MyClock.Ampm.AMPM, 12, 60, 60, true, true))
        ClockList.add(MyClock("その２",MyClock.Ampm.AM, 10, 100, 100, true, false))
        ClockList.save(tMockContext)
        val tExpect = "その１\t2\t12\t60\t60\ttrue\ttrue\nその２\t1\t10\t100\t100\ttrue\tfalse\n"
        assertEquals(tExpect, stream.toString())
    }

    @Test
    fun swapをテストする() {
        ClockList.clear()
        ClockList.add(MyClock("その１",MyClock.Ampm.AMPM, 12, 60, 60, true, true))
        ClockList.add(MyClock("その２",MyClock.Ampm.AMPM, 12, 60, 60, true, true))
        assertEquals("その１",ClockList[0].name)
        assertEquals("その２",ClockList[1].name)
        ClockList.swap(0,1)
        assertEquals("その２",ClockList[0].name)
        assertEquals("その１",ClockList[1].name)
    }
}