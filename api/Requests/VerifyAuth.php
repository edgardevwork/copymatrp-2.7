<?php 
if (!isset($_POST['user_email']) || empty($_POST['user_email'])) {
    exit;
}
$uemail = $_POST['user_email'];

$randomCode = mt_rand(100000, 999999);

require_once __DIR__ . '/vendor/phpmailer/autoload.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

$mail = new PHPMailer(true);
$mail->CharSet = 'utf-8';
$mail->SMTPDebug = 0; 

try {
    $mail->isSMTP();                                      
    $mail->Host = 'mail.crmp.pro';  																							
    $mail->SMTPAuth = true;                               
    $mail->Username = 'auth@crmp.pro'; 
    $mail->Password = 'iH1wK1uO4d'; 
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS;
    $mail->Port = 465; 

    $mail->setFrom('auth@crmp.pro', 'МАТРЕШКА РП');
    $mail->addAddress($uemail);     

    $mail->isHTML(true);                                  
    $mail->Subject = $randomCode.' - код подтверждения';
    
    $mail->Body    = '<html lang="ru">
    <head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <style>
    body {
      width: 100% !important;
      margin: 0;
      padding: 0;
      background-color: #e5e5e5;
    }

    .container {
      width: 100%;
      max-width: 600px;
      margin: 0 auto;
      font-family: tahoma, verdana, segoe, sans-serif;
      padding: 0;
      box-sizing: border-box;
    }

    .header,
    .content,
    .footer {
      width: 100%;
      box-sizing: border-box;
      margin: 20px 0;
      background-color: #ffffff;
      padding: 20px;
    }

    img {
      max-width: 100%;
      height: auto;
    }

    @media only screen and (max-width: 600px) {
      .header,
      .content,
      .footer {
        margin: 10px 0;
        padding: 10px;
      }
    }
  </style>
  </head>
 <div style="width: 100%; font-family: tahoma,verdana,segoe,sans-serif; padding: 0; Margin: 0"> 
            <div style="background-color: #e5e5e5"> 
            <table width="100%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border-spacing: 0px; padding: 0; Margin: 0; width: 100%; height: 100%; background-repeat: repeat; background-position: center top; background-color: #e5e5e5"> 
              <tbody><tr> 
                <td valign="top" style="padding: 0; Margin: 0"> 
                <table class="v1m_5929352369080053265es-header" cellspacing="0" cellpadding="0" align="center" style="border-collapse: collapse; border-spacing: 0px; table-layout: fixed!important; width: 100%; background-color: transparent; background-repeat: repeat; background-position: center top"> 
                  <tbody><tr> 
                    <td align="center" style="padding: 0; Margin: 0"> 
                    <table class="v1m_5929352369080053265es-header-body" cellspacing="0" cellpadding="0" bgcolor="#ffffff" align="center" style="border-collapse: collapse; border-spacing: 0px; background-color: #ffffff; width: 600px"> 
                      <tbody><tr> 
                        <td align="center" style="Margin: 0; padding-left: 20px; padding-right: 20px; padding-top: 40px; padding-bottom: 40px; background-image: url(https://crmp.pro/files/matrp/space/email/client/bg_header.png); background-repeat: no-repeat; background-position: left top" background="https://crmp.pro/files/matrp/space/email/client/bg_header.png">
                        <table cellspacing="0" cellpadding="0" width="100%" style="border-collapse: collapse; border-spacing: 0px"> 
                          <tbody><tr> 
                            <td align="center" style="padding: 0; Margin: 0; width: 560px"> 
                            <table width="100%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border-spacing: 0px"> 
                              <tbody><tr> 
                                <td align="center" style="padding: 0; Margin: 0; font-size: 0px"><a href="https://matrp.ru/" style="text-decoration: underline; color: #2cb543; font-size: 14px" rel="noreferrer" target="_blank"><img class="v1m_5929352369080053265adapt-img" src="https://crmp.pro/files/matrp/space/email/parnter/logo.png" style="display: block; border: 0; outline: none; text-decoration: none" width="256"></a></td>
                              </tr> 
                            </tbody></table></td> 
                          </tr> 
                        </tbody></table></td> 
                      </tr> 
                    </tbody></table></td> 
                  </tr> 
                </tbody></table> 
                <table class="v1m_5929352369080053265es-content" cellspacing="0" cellpadding="0" align="center" style="border-collapse: collapse; border-spacing: 0px; table-layout: fixed!important; width: 100%"> 
                  <tbody><tr> 
                    <td align="center" style="padding: 0; Margin: 0"> 
                    <table class="v1m_5929352369080053265es-content-body" cellspacing="0" cellpadding="0" bgcolor="#ffffff" align="center" style="border-collapse: collapse; border-spacing: 0px; background-color: #ffffff; width: 600px"> 
                      <tbody><tr> 
                        <td align="center" style="padding: 0; Margin: 0; padding-left: 20px; padding-right: 20px; padding-top: 40px"> 
                        <table width="100%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border-spacing: 0px"> 
                          <tbody><tr> 
                            <td valign="top" align="center" style="padding: 0; Margin: 0; width: 560px"> 
                            <table width="100%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border-spacing: 0px"> 
                              <tbody><tr> 
                                <td align="center" style="padding: 0; Margin: 0"><p style="Margin: 0; font-family: lucida sans unicode,lucida grande,sans-serif; line-height: 24px; color: #1d1620; font-size: 16px"><strong>ВАШ КОД</strong></p></td> 
                              </tr> 
                            </tbody></table></td> 
                          </tr> 
                        </tbody></table></td> 
                      </tr> 
                    </tbody></table></td> 
                  </tr> 
                </tbody></table> 
                <table class="v1m_5929352369080053265es-footer" cellspacing="0" cellpadding="0" align="center" style="border-collapse: collapse; border-spacing: 0px; table-layout: fixed!important; width: 100%; background-color: transparent; background-repeat: repeat; background-position: center top"> 
                  <tbody><tr> 
                    <td align="center" style="padding: 0; Margin: 0"> 
                    <table class="v1m_5929352369080053265es-footer-body" cellspacing="0" cellpadding="0" bgcolor="#ffffff" align="center" style="border-collapse: collapse; border-spacing: 0px; background-color: #ffffff; width: 600px"> 
                      <tbody><tr> 
                        <td align="center" style="padding: 0; Margin: 0; padding-top: 25px"> 
                        <table cellspacing="0" cellpadding="0" width="100%" style="border-collapse: collapse; border-spacing: 0px"> 
                          <tbody><tr> 
                            <td align="center" style="padding: 0; Margin: 0; width: 600px"> 
                            <table width="100%" cellspacing="0" cellpadding="0" background="https://crmp.pro/files/matrp/space/email/client/bg_text.png" style="border-collapse: collapse; border-spacing: 0px; background-image: url(https://crmp.pro/files/matrp/space/email/client/bg_text.png); background-repeat: no-repeat; background-position: center top; background-size: contain">
                              <tbody><tr> 
                                <td align="center" style="padding: 0; Margin: 0; padding-bottom: 25px; padding-top: 30px"><h2 style="Margin: 0; line-height: 29px; font-family: lucida sans unicode,lucida grande,sans-serif; font-size: 24px; font-style: normal; font-weight: normal; color: #333333; text-align: center; text-alHhgn: center" ><strong>'.$randomCode.'</strong></h2></td> 
                              </tr> 
                            </tbody></table></td> 
                          </tr> 
                        </tbody></table></td> 
                      </tr> 
                      <tr> 
                        <td align="center" style="padding: 0; Margin: 0; padding-top: 5px; padding-bottom: 5px"> 
                        <table width="100%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border-spacing: 0px"> 
                          <tbody><tr> 
                            <td valign="top" align="center" style="padding: 0; Margin: 0; width: 600px"> 
                            <table width="100%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border-spacing: 0px"> 
                              <tbody><tr> 
                                <td align="center" style="padding: 0; Margin: 0; padding-bottom: 20px; padding-top: 30px"><p style="Margin: 0; font-family: lucida sans unicode,lucida grande,sans-serif; line-height: 18px; color: #aaa9a9; font-size: 12px">Код действителен в течении 5-и минут после получения</p></td> 
                              </tr> 
                              <tr> 
                                <td style="padding: 0; Margin: 0"> 
                                <table class="v1m_5929352369080053265es-menu" width="100%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border-spacing: 0px"> 
                                  <tbody><tr> 
                                    <td style="Margin: 0; padding-left: 5px; padding-right: 5px; padding-top: 40px; padding-bottom: 10px; border: 0" width="33.33%" bgcolor="transparent" align="center"><a style="text-decoration: none; display: block; font-family: lucida sans unicode,lucida grande,sans-serif; color: #cbc8cd; font-size: 14px" href="https://matrp.ru/" rel="noreferrer" target="_blank">Сайт</a></td> 
                                    <td style="Margin: 0; padding-left: 5px; padding-right: 5px; padding-top: 40px; padding-bottom: 10px; border: 0" width="33.33%" bgcolor="transparent" align="center"><a style="text-decoration: none; display: block; font-family: lucida sans unicode,lucida grande,sans-serif; color: #cbc8cd; font-size: 14px" href="https://forum.matrp.ru/index.php" rel="noreferrer" target="_blank">Форум</a></td> 
                                    <td style="Margin: 0; padding-left: 5px; padding-right: 5px; padding-top: 40px; padding-bottom: 10px; border: 0" width="33.33%" bgcolor="transparent" align="center"><a style="text-decoration: none; display: block; font-family: lucida sans unicode,lucida grande,sans-serif; color: #cbc8cd; font-size: 14px" href="https://matrp.ru/play" rel="noreferrer" target="_blank">Начать играть</a></td> 
                                  </tr> 
                                </tbody></table></td> 
                              </tr> 
                            </tbody></table></td> 
                          </tr> 
                        </tbody></table></td> 
                      </tr> 
                    </tbody></table></td> 
                  </tr> 
                </tbody></table></td> 
              </tr> 
            </tbody></table> 
            </div>  
          </div>
</body>
</html>';

$mail->send();
echo $randomCode;
} catch (Exception $e) {
    // В случае критического сбоя SMTP выполнение тихо прекращается
    //echo "Err";
    exit;
}
?>