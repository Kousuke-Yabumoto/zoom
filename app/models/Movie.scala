package models

import java.util.UUID

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.transfer.{Upload, TransferManager}
import com.amazonaws.event._
import consts._
import java.io.File
import com.amazonaws.services.s3.AmazonS3
import play.api.mvc._

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GroupGrantee, AccessControlList, PutObjectRequest, Permission}

import models.dao.{Movie => MovieDao}

/**
 * Created by z00036 on 2015/05/25.
 */
case class MovieForm(
  title: String,
  explain: String
)
case class MovieFile(
  file: File,
  filename: String,
  contentType: Option[String]
)

object Movie {

  val s3client = new AmazonS3Client(new ProfileCredentialsProvider())
  val bucketName: String = AppConfig.s3.bucketName
  val accessKey: String = AppConfig.s3.accessKey
  val secretKey: String = AppConfig.s3.secretKey
  val s3Path: String = AppConfig.s3.path
  val s3Url: String = s"https://s3-ap-northeast-1.amazonaws.com/%s/%s"

  def uploadToS3(file: File, contentType: String) = {
    // S3アップロード
    val credentials = new BasicAWSCredentials(accessKey, secretKey);
    val transferManager = new TransferManager(credentials);
    val path = s3Path.format(UUID.randomUUID.toString, contentType.split("/")(1))
    val acl = new AccessControlList
    acl.grantPermission(GroupGrantee.AllUsers, Permission.Read)
    try {
      val request = new PutObjectRequest(bucketName, path, file).withAccessControlList(acl)
      val upload = transferManager.upload(request)
      upload.waitForCompletion()
    } finally {
      transferManager.shutdownNow()
    }
    s3Url.format(bucketName, path)
  }

  // アップロード処理
  def upload(form: MovieForm, file: Option[MovieFile]): Option[MovieDao] = {
    val url = for {
      f <- file
      contentType <- f.contentType
    } yield uploadToS3(f.file, contentType)
    MovieDao.insert(form.title, form.explain, file.map(_.filename), file.flatMap(_.contentType), url)
  }

  def findMivies(info: AuthInfo) = {
  }
}
