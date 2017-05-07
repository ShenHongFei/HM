package ueditor

import config.WebAppConfig
import org.apache.commons.codec.binary.Base64
import org.apache.commons.fileupload.FileUploadException
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import ueditor.util.PathFormat
import ueditor.defination.AppInfo
import ueditor.defination.BaseState
import ueditor.defination.FileType
import ueditor.defination.State
import ueditor.util.StorageManager

import javax.servlet.http.HttpServletRequest
import java.nio.file.Path

class Uploader {
	private HttpServletRequest request = null
    private Map<String, Object> conf = null
    File storeDir

    Uploader(HttpServletRequest request,Map<String, Object> conf,File storeDir) {
		this.request = request
        this.conf = conf
        this.storeDir=storeDir
    }

    final State doExec() {
		State state
        if ("true"==conf.get("isBase64")) {
			state = Base64Uploader.save(request,conf)
        } else {
			state = BinaryUploader.save(request, conf,storeDir)
        }
		return state
    }

    class BinaryUploader {

        static final State save(HttpServletRequest request,Map<String,Object> conf,File storeDir){
            boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null
            if (!ServletFileUpload.isMultipartContent(request)) {
                return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT)
            }
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory())
            if ( isAjaxUpload ) {
                upload.setHeaderEncoding( "UTF-8" )
            }
            try {
                MultipartFile f = ((MultipartHttpServletRequest)request).getFile(conf.get("fieldName").toString())
                String fn = f.originalFilename
                fn=fn.replaceAll(/[\\/:*?"<>| ]/,'-')
                String suffix = '.'+fn.split('\\.')[-1]
                if(f.size>(Long) conf.get("maxSize")) return new BaseState(false, AppInfo.MAX_SIZE)
                if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
                    return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE)
                }
                if(!storeDir.exists()){
                    storeDir.mkdir()
                }
                File tf = new File(storeDir,fn)
                if(tf.exists()) tf.delete()
                f.transferTo(tf)
                State st = new BaseState(true)
                String relativePath = WebAppConfig.FILE_DIR.toPath().relativize(tf.toPath()).toString().replace('\\','/')
                st.putInfo('size',tf.length())
                st.putInfo('title',tf.name)
                st.putInfo("url", "file/$relativePath")
                st.putInfo("type", suffix)
                st.putInfo("original", fn)
                return st
            } catch (FileUploadException e) {
                e.printStackTrace()
                return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR)
            } catch (IOException e) {
                e.printStackTrace()
            }
            return new BaseState(false, AppInfo.IO_ERROR)
        }

        private static boolean validType(String type, String[] allowTypes) {
            List<String> list = Arrays.asList(allowTypes)

            return list.contains(type)
        }
    }

    class Base64Uploader {

        static State save(HttpServletRequest request, Map<String, Object> conf) {
            String filedName = (String) conf.get("fieldName")
            String fileName = request.getParameter(filedName)
            byte[] data = decode(fileName)

            long maxSize = ((Long) conf.get("maxSize")).longValue()

            if (!validSize(data, maxSize)) {
                return new BaseState(false, AppInfo.MAX_SIZE)
            }

            String suffix = FileType.getSuffix("JPG")

            String savePath = PathFormat.parse((String) conf.get("savePath"),
                    (String) conf.get("filename"))

            savePath = savePath + suffix
            String rootPath = UEditorConfig.getRootPath(request,conf)
            String physicalPath = rootPath + savePath

            State storageState = StorageManager.saveBinaryFile(data, physicalPath)

            if (storageState.isSuccess()) {
                storageState.putInfo("url", PathFormat.format(savePath))
                storageState.putInfo("type", suffix)
                storageState.putInfo("original", "")
            }

            return storageState
        }

        private static byte[] decode(String content) {
            return Base64.decodeBase64(content)
        }

        private static boolean validSize(byte[] data, long length) {
            return data.length <= length
        }

    }
}
