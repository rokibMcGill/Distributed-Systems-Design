package FaultToleranceDPSS;

/**
* FaultToleranceDPSS/DPSS_FTHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from FaultToleranceDPSS.idl
* Sunday, August 2, 2020 5:22:25 AM EDT
*/

public final class DPSS_FTHolder implements org.omg.CORBA.portable.Streamable
{
  public FaultToleranceDPSS.DPSS_FT value = null;

  public DPSS_FTHolder ()
  {
  }

  public DPSS_FTHolder (FaultToleranceDPSS.DPSS_FT initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = FaultToleranceDPSS.DPSS_FTHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    FaultToleranceDPSS.DPSS_FTHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return FaultToleranceDPSS.DPSS_FTHelper.type ();
  }

}
