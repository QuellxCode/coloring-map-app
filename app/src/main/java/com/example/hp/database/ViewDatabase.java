package com.example.hp.database;

class ViewDatabase {
    private String iname,iemail;
    public ViewDatabase(String iname, String iemail) {
      this.iname = iname;
      this.iemail= iemail;

    }

    public String getIname() {
        return iname;
    }

    public void setIname(String iname) {
        this.iname = iname;
    }

    public String getIemail() {
        return iemail;
    }

    public void setIemail(String iemail) {
        this.iemail = iemail;
    }
}
