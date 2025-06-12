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
- ทำการกด Go to console ที่มุมขวาบนจากนั้น Login เข้าระบบผ่าน Google Account
![Firebase Main Page](https://drive.google.com/uc?export=view&id=1yJdGO0JiEPpCYGkHWQBHs45o2MAy-c_x)
- เมื่อเข้าสู่ระบบสำเร็จให้ทำการกดสร้าง Project
![Firebase Create Project](https://drive.google.com/uc?export=view&id=1I5aHskPZn0bFdC2keQ4OSWm8UNS31kA5)
- ทำการใส่ชื่อของโปรเจคที่ต้องการสร้างจากนั้นกด Continue ไปเรื่อยๆ แล้วกด Default Account for Firebase แล้วกด Create Project
- เมื่อทำการสร้าง Project สำเร็จจะขึ้นหน้าดังนี้
![Firebase Dashboard](https://drive.google.com/uc?export=view&id=11i3w_Kpw1TbWL2E0ANZF9Bw8EUWjw7qU)
- หลังจากนั้นให้ทำการกดไปที่ Build Authentication โดยเลือก Sign-in method เป็น Email/Password
![Firebase Authentication](https://drive.google.com/uc?export=view&id=1TfBSUKgQZmAqJAyGiZhqlIJXJhZVgPTx)
- ต่อมาให้ทำการกด Build Realtime Database เพื่อเก็บข้อมูล Database ที่เราจะใช้ โดยให้เลือกเก็บข้อมูลที่ Singapore และ Security rules ให้เลือกสร้างใน Test Mode
![Firebase Realtime Database](https://drive.google.com/uc?export=view&id=1jhW0ahyxO0VD15OjUjfCpUSLTtSyF958)
- เมื่อทำทุกอย่างพร้อมแล้วให้กลับไปที่ Project Overview จากนั้นเลือกไปที่ Icon Android เพื่อทำการเพิ่ม Firebase ไปใน Application
- กรอกข้อมูลรายละเอียดตัวแอปที่เชื่อม โดยจำเป็นต้องกำหนด Android Package Name เป็น com.example.homstray ตามในรูปข้างล่าง
![Firebase Connect Application](https://drive.google.com/uc?export=view&id=1YgHCyc69lb-ormFmg3dr7kS0XdWNW0S5)
- ทำการ Download google-services.json แล้วกด Next เรื่อยๆ แล้วกด Continue to Console เพื่อกลับไปหน้าเดิม
## สร้าง Api ของ ImgBB เพื่อ Hosting รูปภาพ
**2. สร้าง Api ของ ImgBB**
- เข้าไปยังเว็ปไซต์ [ImgBB](https://imgbb.com/) หรือเข้าสู่เว็ปไซต์ผ่าน Url ด้านล่าง
```
https://imgbb.com/
```
- เมื่อเข้าไปยังเว็ปไซต์ทำการสมัครสมาชิกแล้วทำการเข้าสู่ระบบ
- เมื่อเข้าสู่ระบบเรียบร้อยแล้วทำการกดเครื่องหมายคำถามในมุมซ้ายบนจากนั้นกดไปที่ API
- ทำการกด Add API Key จากนั้น Save ไว้ใช้ในภายหลัง
## เชื่อมต่อกับ Geocoding Api ใน Google Console เพื่อหาแปลงตำแหน่งที่อยู่เป็น Longtitude และ Latitude และสร้างค่า Api มาใช้กับแอปพลิเคชัน
**3. สร้างโปรเจคใน Google Console**
- เข้าไปยังเว็ปไซต์ [Google Console](https://cloud.google.com/free?utm_source=PMAX&utm_medium=display&utm_campaign=FY25-Q1-apac-gcp-DR-campaign-TH&utm_content=th-en&utm_source=PMAX&utm_medium=PMAX&utm_campaign=FY24-H2-apac-gcp-DR-campaign-TH&utm_content=th-en&&https://ad.doubleclick.net/ddm/trackclk/N5295.276639.GOOGLEADWORDS/B26943865.344601469;dc_trk_aid=535898303;dc_trk_cid=163098484;dc_lat=;dc_rdid=;tag_for_child_directed_treatment=;tfua=;ltd=&gad_source=1&gad_campaignid=22046808695&gclid=Cj0KCQjw0qTCBhCmARIsAAj8C4YxIqZPg3YilVJFOYKhjngdv3One1z7NxAdq8SoCOUYqCca5QztSowaAvUuEALw_wcB&gclsrc=aw.ds) หรือเข้าสู่เว็ปไซต์ผ่าน Url ด้านล่าง
```
https://cloud.google.com/free?utm_source=PMAX&utm_medium=display&utm_campaign=FY25-Q1-apac-gcp-DR-campaign-TH&utm_content=th-en&utm_source=PMAX&utm_medium=PMAX&utm_campaign=FY24-H2-apac-gcp-DR-campaign-TH&utm_content=th-en&&https://ad.doubleclick.net/ddm/trackclk/N5295.276639.GOOGLEADWORDS/B26943865.344601469;dc_trk_aid=535898303;dc_trk_cid=163098484;dc_lat=;dc_rdid=;tag_for_child_directed_treatment=;tfua=;ltd=&gad_source=1&gad_campaignid=22046808695&gclid=Cj0KCQjw0qTCBhCmARIsAAj8C4YxIqZPg3YilVJFOYKhjngdv3One1z7NxAdq8SoCOUYqCca5QztSowaAvUuEALw_wcB&gclsrc=aw.ds
```
- เมื่อเข้าสู่หน้าเว็ปไซต์ให้ไปที่หน้า Console โดย Login ผ่านทาง Google
- กดสร้าง Project จากนั้นค้นหา Geocoding API
![Google Cloud Create Project](https://drive.google.com/uc?export=view&id=1RKja-8_wDlzyKzI1xLypZANKQ9W3aLYY)
- กด Enable API จากนั้น Copy API เพื่อนำไปใช้ใน Android Studio
![Google Cloud Enable API](https://drive.google.com/uc?export=view&id=1M27jbcK4-QCcITz2_al8Tqjs6Xqy9gtm)

# ขั้นตอนที่ 2 ติดตั้งโปรแกรมที่จำเป็น
## ติดตั้ง Git เพื่อ Clone Repository
**1. ติดตั้งโปรแกรม Git**
- เข้าไปดาวโหลดโปรแกรมผ่านเว็ปไซต์ [Git](https://git-scm.com/) หรือเข้าสู่เว็ปไซต์ผ่าน Url ด้านล่าง
```
https://git-scm.com/
```
- เมื่อเข้าสู่หน้าเว็ปไซต์ให้ทำการกดที่ Download\
![Git Main Page](https://drive.google.com/uc?export=view&id=1xy2BrmM0hYx_6m7kSLegLN8oc72J10XS)
- หลังจากที่ได้กดเข้าไปที่หน้า Download ให้ทำการเลือก Download Git จากระบบปฏิบัติการเครื่องที่ใช้อยู่ในปัจจุบัน\
![Git Download Page](https://drive.google.com/uc?export=view&id=1lXGL0qDrE3yf3tMZBLDU4HnX2wo6cOqS)
- หลังจากที่ได้ File ติดตั้งให้ทำการกดเปิดแล้วกด Next ไปเรื่อยๆ จน Install สำเร็จ\
- เมื่อทำการติดตั้งสำเร็จให้เปิด Git Bash ขึ้นมาจากนั้นทำการพิมพ์คำสั่งในข้อความด้านล่างเพื่อเป็นการเช็คว่ามีการติดตั้งสำเร็จ โดยหากมีการแสดงผลดังรูปด้านล่างถือว่ามีการติดตั้ง Git สำเร็จเรียบร้อย
```
git --version
```
![Git Check Version](https://drive.google.com/uc?export=view&id=1dGoN2uVQBopQDmp09qz241GcJqdFXR_7)
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

หากเจอ error แบบ Missing SDK, ให้ไปที่:
- Settings > Appearance & Behavior > System Settings > Android SDK
- ติดตั้ง SDK Platform เช่น Android 13 หรือ 14

⚠ ปัญหาที่พบบ่อย\
❌ Gradle Sync Failed: ให้กด "Try again" หรือเช็กว่าเน็ตเสถียร\
❌ Emulator ไม่เปิด: เช็กว่าเปิด Virtualization (VT) ใน BIOS แล้วหรือยัง\
❌ Build Failed: ลอง File > Invalidate Caches / Restart

# ขั้นตอนที่ 3 เชื่อม API, Firebase เข้ากับ Android App
## ใช้ Git เพื่อ Clone Repository
**1. Clone Android App มาไว้ที่เครื่อง**
- สร้าง Folder ที่จะใช้เก็บตัวแอปพลิเคชัน
- Shift + คลิกขวาที่ภายใน Folder จากนั้นทำการเลือก Open Git Bash Here
![Git Open Bash Here](https://drive.google.com/uc?export=view&id=1PX82I9QeSYKWprEkBDFSolGrMq6ApXKc)
- เมื่อ Git Bash เปิดขึ้น ใช้คำสั่งด้านล่าง ดังนี้
```
git clone https://github.com/ComSciThammasatU/2567-2-cs403-final-submission-67-1_25_lpp-s1.git
```
- เมื่อ Git Cloning สำเร็จควรจะมี Folder ใหม่โผล่ขึ้นมา
![Git Clone](https://drive.google.com/uc?export=view&id=1DdFPVkp51b4DNqFXeJ63-EVL0Uhx275a)
## เปิด Application ใน Android Studio
**2. เปิด Application ใน Android Studio**
- เปิด Android Studio ขึ้นมา
- คลิก File -> New -> Import Project
- เลือก Folder ที่พึ่งใช้ Git Clone เข้ามา
![Git Clone](https://drive.google.com/uc?export=view&id=19Mt_a6nUwtb8LwSh91NkXRzkPbit3nbK)
## เชื่อม API และ Firebase เข้ากับ Android Studio
**3. นำ API ที่เก็บไว้จากขั้นตอนก่อนหน้าและ google-services.json มาใช้กับ Android Studio**
- นำ google-services.json ที่ได้จาก Firebase ไปใส่ใน app ของ Android Studio
![Add Google Services](https://drive.google.com/uc?export=view&id=1ppkhFQsuQJDrnHIPQRstcXU8LUVq47o0)
- ทำการรันตัวแอปผ่าน Virtual Device แล้วเช็ค Firebase Realtime Database เพื่อตรวจสอบว่ามีการเชื่อมกับตัวแอปจริง
![Check Realtime Database](https://drive.google.com/uc?export=view&id=1_kO6zG3sqzKlKmczujxfoIV_AXHQDw4_)
- กดสร้าง Account Admin โดยใส่ Email และ Password(ตัวแอปยังไม่มีการยืนยันตัวตน)
![Create Account Admin](https://drive.google.com/uc?export=view&id=1qItslRl3sCtW0NxxdEVktwa0lgVSZZEp)
- ตรวจสอบหน้า Firebase Authentication เพื่อเช็คว่ามี User ขึ้นหรือไม่
![Firebase Authentication](https://drive.google.com/uc?export=view&id=1DrU9oq1IWlOhfYSdIv67rF2QBJce4s2V)
- Copy User Id จากนั้นนำมาใส่ใน Android Studio ที่ gradle.properties รวมถึง API ต่างๆของ Google Cloud และ ImgBB ที่บันทึกไว้จากขั้นตอนก่อนหน้า
![Put API In Android Studio](https://drive.google.com/uc?export=view&id=1lT7YqPYBea5u_JOlHVivEHW4pjFTTTwK)
- เสร็จสิ้นการทำเตรียมความพร้อม
# ขั้นตอนที่ 4 การรันแอปพลิเคชัน
## การรันทดสอบแอปพลิเคชัน
**1. รันผ่าน Virtual Device ใน Android Studio**\
จากขั้นตอนที่ 2.2 ให้ทำการกด Run App ผ่านตัวโปรแกรม Android Studio ได้เลย\
**2. รันผ่านโทรศัพท์ Android**
- ให้เข้าไปที่ Setting แล้วเลื่อนลงไปข้างล่างจนกว่าจะเห็น About Phone
- กดเข้าไปแล้วเลือก Software information
- กดที่ Build number 5 - 6 ครั้งหรือหลายๆครั้งติดต่อกัน
- กดยืนยันเปิด Developer Mode แล้วให้ไปที่หน้า Developer options ที่จะขึ้นมาใหม่หลังมีการเปิด Developer mode
- เลื่อนไปที่หัวข้อ Debugging จากนั้นทำการเปิด USB debugging หรือ Wireless debugging เพื่อทำการเชื่อมเข้ากับ Android Studio

# วิธีการใช้งานแอปพลิเคชัน
**ส่วนของ User**
- หน้า Register ไว้สมัครสมาชิกเข้าสู่ระบบ
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1Iwgqbu_Mz79JV31LFc1G8K4w8lfALSbq" width="300">
</p>
- หน้า Login ไว้เข้าสู่ระบบ
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=12LkPY_wYk7stTNxn1F9qqMGeJfnJBolS" width="300">
</p>
- หน้ากรอกประวัติส่วนตัว ไว้กรอกประวัติส่วนตัวผู้ใช้เมื่อเข้าสู่แอปครั้งแรก
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1b4RMePBMn_7YK_6S0uNR-Tp3pgLdeLgp" width="300">
</p>
- หน้าแสดงประวัติผู้ใช้ ไว้แสดงประวัติข้อมูลส่วนตัวของผู้ใช้
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1hnK9jYY20earA-131VPSUBZk_Zyx863s" width="300">
</p>
- หน้าแก้ไขประวัติผู้ใช้ ไว้แก้ไขประวัติข้อมูลส่วนตัวของผู้ใช้
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1hTc1xOa5M2rHTKnxnIpkJF7nAHYElKF_" width="300">
</p>
- หน้า Preference ไว้เก็บข้อมูลความชอบสัตว์ของผู้ใช้
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1Lp6qszNE2qHmqPA5McLVh6dlii8JjI84" width="300">
</p>
- หน้าหลักไว้ปัด Profile สัตว์หาสัตว์ที่ถูกใจ
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1g4WIOipjBbR4iGI7B9EOAWec8hQbsAR4" width="300">
</p>
- หน้า Liked Animal ไว้ดูสัตว์ที่ผู้ใช้เคยกดถูกใจ
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1LjD4DvOM0NBIm3q0TGOm3Jg-O9LPIMvK" width="300">
</p>
- หน้าแสดงข้อมูลสัตว์ ไว้ดูข้อมูลรายละเอียดของสัตว์ที่ผู้ใช้ถูกใจ
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1LjD4DvOM0NBIm3q0TGOm3Jg-O9LPIMvK" width="300">
</p>

**ส่วนของ Admin**
- หน้าหลักของ Admin ไว้ดูสัตว์ที่อยู่ในระบบ
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=17PW6oM0FBvW1MKcPLDR5VVFhyX9YItg4" width="300">
</p>
- หน้าสร้าง Profile สัตว์ ไว้สร้างสัตว์เข้าไปในระบบ
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1LdhB-ZXcFaDNk1Bwrf-zTroadvnmpGVN" width="300">
</p>
- หน้าแสดงข้อมูลสัตว์ ไว้แสดงข้อมูลสัตว์ที่สร้าง โดยมีปุ่มไว้ลบและแก้ไขข้อมูลสัตว์
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1fiaXrsHXpQn61NpLGb4sDbxK6YlhCb3L" width="300">
</p>
- หน้าแก้ไขข้อมูลสัตว์ ไว้แก้ไขข้อมูลสัตว์ที่เคยสร้าง โดยมีหน้าตาเหมือนกับหน้าสร้าง Profile สัตว์
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1aJ9zE7IxwafYYzAamzslHLDhzDQL4EcN" width="300">
</p>
- หน้าแก้ไขประวัติผู้ใช้ ไว้แก้ไขประวัติข้อมูลส่วนตัวของผู้ใช้
<p align="center">
    <img src="https://drive.google.com/uc?export=view&id=1hTc1xOa5M2rHTKnxnIpkJF7nAHYElKF_" width="300">
</p>

# ⚠หมายเหตุ
Use case ในส่วนขั้นตอนอุปการะสัตว์ยังทำไม่สำเร็จเนื่องจากทางผู้จัดทำจัดการเวลาได้ไม่ดีทำให้ทางโปรแกรมยังไม่เสร็จสมบูรณ์ตามที่ตั้งเป้าหมายไว้

# ช่องทางติดต่อ
หาก Application มีข้อผิดพลาดตรงไหนสามารถติดต่อได้ผ่าน Email : visarut.poo@dome.tu.ac.th หรือผ่านช่องทางทีม
