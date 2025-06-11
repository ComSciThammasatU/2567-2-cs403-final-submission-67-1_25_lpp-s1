[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/w8H8oomW)
**<ins>Note</ins>: Students must update this `README.md` file to be an installation manual or a README file for their own CS403 projects.**

**รหัสโครงงาน:** 67-1_25_lpp-s1

**ชื่อโครงงาน (ไทย):** แอปพลิเคชันหาบ้านให้สัตว์จรจัด

**Project Title (Eng):** Home Stray 

**อาจารย์ที่ปรึกษาโครงงาน:** ผศ. ดร.ลัมพาพรรณ พันธ์ชูจิตร์

**ผู้จัดทำโครงงาน:**
1. นายวิศรุต พูลศิริ  6309650866  visarut.poo@dome.tu.ac.th

# ขั้นตอนที่ 1 เตรียมความพร้อมก่อนติดตั้งโปรแกรม
## เตรียมโปรเจค Firebase ไว้ใช้เก็บฐานข้อมูลแอปพลิเคชันใน Firebase Realtime Database
**1. สร้างโปรเจคใน Firebase Console**
- เข้าไปยังเว็ปไซต์ [Firebase](https://firebase.google.com/?gad_source=1&gad_campaignid=20100026061&gbraid=0AAAAADpUDOgi4Z92g-9ZqOGA-goQZMVrf&gclid=Cj0KCQjw0qTCBhCmARIsAAj8C4bSVUxeiQl7VJQrjUSN7xuvzA7VaHhArHGpOuLgTlwKE0ZTG63FEI8aAry4EALw_wcB&gclsrc=aw.ds) หรือเข้าสู่เว็ปไซต์ผ่าน Url ด้านล่าง
```
https://firebase.google.com/?gad_source=1&gad_campaignid=20100026061&gbraid=0AAAAADpUDOgi4Z92g-9ZqOGA-goQZMVrf&gclid=Cj0KCQjw0qTCBhCmARIsAAj8C4bSVUxeiQl7VJQrjUSN7xuvzA7VaHhArHGpOuLgTlwKE0ZTG63FEI8aAry4EALw_wcB&gclsrc=aw.ds
```
## สร้าง Api ของ ImgBB เพื่อ Hosting รูปภาพ
**2. สร้าง Api ของ ImgBB**
- เข้าไปยังเว็ปไซต์ [ImgBB](https://imgbb.com/) หรือเข้าสู่เว็ปไซต์ผ่าน Url ด้านล่าง
```
https://imgbb.com/
```
## เชื่อมต่อกับ Geocoding Api ใน Google Console เพื่อหาแปลงตำแหน่งที่อยู่เป็น Longtitude และ Latitude และสร้างค่า Api มาใช้กับแอปพลิเคชัน
**3. สร้างโปรเจคใน Google Console**
- เข้าไปยังเว็ปไซต์ [ImgBB](https://cloud.google.com/free?utm_source=PMAX&utm_medium=display&utm_campaign=FY25-Q1-apac-gcp-DR-campaign-TH&utm_content=th-en&utm_source=PMAX&utm_medium=PMAX&utm_campaign=FY24-H2-apac-gcp-DR-campaign-TH&utm_content=th-en&&https://ad.doubleclick.net/ddm/trackclk/N5295.276639.GOOGLEADWORDS/B26943865.344601469;dc_trk_aid=535898303;dc_trk_cid=163098484;dc_lat=;dc_rdid=;tag_for_child_directed_treatment=;tfua=;ltd=&gad_source=1&gad_campaignid=22046808695&gclid=Cj0KCQjw0qTCBhCmARIsAAj8C4YxIqZPg3YilVJFOYKhjngdv3One1z7NxAdq8SoCOUYqCca5QztSowaAvUuEALw_wcB&gclsrc=aw.ds) หรือเข้าสู่เว็ปไซต์ผ่าน Url ด้านล่าง
```
https://cloud.google.com/free?utm_source=PMAX&utm_medium=display&utm_campaign=FY25-Q1-apac-gcp-DR-campaign-TH&utm_content=th-en&utm_source=PMAX&utm_medium=PMAX&utm_campaign=FY24-H2-apac-gcp-DR-campaign-TH&utm_content=th-en&&https://ad.doubleclick.net/ddm/trackclk/N5295.276639.GOOGLEADWORDS/B26943865.344601469;dc_trk_aid=535898303;dc_trk_cid=163098484;dc_lat=;dc_rdid=;tag_for_child_directed_treatment=;tfua=;ltd=&gad_source=1&gad_campaignid=22046808695&gclid=Cj0KCQjw0qTCBhCmARIsAAj8C4YxIqZPg3YilVJFOYKhjngdv3One1z7NxAdq8SoCOUYqCca5QztSowaAvUuEALw_wcB&gclsrc=aw.ds
```

# ขั้นตอนที่ 2 ติดตั้งโปรแกรมที่จำเป็น
## ติดตั้ง Git เพื่อ Clone Repository
**1. ติดตั้งโปรแกรม Git**
- เข้าไปดาวโหลดโปรแกรมผ่านเว็ปไซต์ [Git](https://git-scm.com/) หรือเข้าสู่เว็ปไซต์ผ่าน Url ด้านล่าง
```
https://git-scm.com/
```
- เมื่อเข้าสู่หน้าเว็ปไซต์ให้ทำการกดที่ Download\
![Git Main Page](https://i.ibb.co/Fkhk5dJ5/git1.png)
- หลังจากที่ได้กดเข้าไปที่หน้า Download ให้ทำการเลือก Download Git จากระบบปฏิบัติการเครื่องที่ใช้อยู่ในปัจจุบัน\
![Git Download Page](https://i.ibb.co/7xVpL3Yj/git2.png)
- หลังจากที่ได้ File ติดตั้งให้ทำการกดเปิดแล้วกด Next ไปเรื่อยๆ จน Install สำเร็จ\
- เมื่อทำการติดตั้งสำเร็จให้เปิด Git Bash ขึ้นมาจากนั้นทำการพิมพ์คำสั่งในข้อความด้านล่างเพื่อเป็นการเช็คว่ามีการติดตั้งสำเร็จ โดยหากมีการแสดงผลดังรูปด้านล่างถือว่ามีการติดตั้ง Git สำเร็จเรียบร้อย
```
git --version
```
![Git Check Version](https://i.ibb.co/6R1K33t3/git3.png)
## ติดตั้ง Android Studio ไว้รันแอปพลิเคชัน
**2. ติดตั้งโปรแกรม Android Studio**
- เข้าไปดาวโหลดโปรแกรมผ่านเว็ปไซต์ [Android Studio](https://developer.android.com/studio) หรือเข้าสู่เว็ปไซต์ผ่าน Url ด้านล่าง
```
https://developer.android.com/studio
```
- เมื่อเข้าสู่เว็ปไซต์ให้ทำการ Download Android Studio จากนั้นทำการติดตั้งโปรแกรม
- กด Next เรื่อยๆ จนโปรแกรมติดตั้งสำเร็จ
- เมื่อเปิดโปรแกรมได้แล้วให้เข้าไปที่ Device Manager จากนั้นทำการเลือก Create Virtual Device เพื่อทำการรันแอปพลิเคชันในกรณีที่ไม่มี Android
- กดเลือก Device ที่ต้องการสร้างโดยในที่นี้แนะนำให้ใช้ Medium Phone API 35
# Topic 3
