var http = require('http');
var url = require('url');
var fs = require('fs');

const express = require('express');
const app = express();
var multiparty = require('multiparty');

app.post('/uploadvideo',(req,res)=>{
	console.log('who get in here uploadvideo /users');
	
	var form = new multiparty.Form();


	// get field name & value
	form.on('field', function(name, value){
		console.log('normal field / name = ' + name + ', value = ' + value);
	});

	// file upload handling
	form.on('part', function(part){
		var filename;
		var size;
		if(part.filename){
			filename = part.filename.split('/')[2];
			size = part.byteCount;
		}

		else{
			part.resume();
		}

		console.log("Write Streaming file :"+filename);
		var writeStream = fs.createWriteStream('/Users/q/test_music/'+filename);
		writeStream.filename = filename;
		part.pipe(writeStream);

		part.on('data',function(chunk){
			console.log(filename+' read : ' + chunk.length + 'bytes');
		});

		part.on('end',function(){
			console.log(filename+' Part read complete');
			writeStream.end();
		});

	});

	// all uploads are completed
	form.on('close', function(){
		res.status(200).send('Upload complete');
	});

	// track progress
	form.on('progress', function(byteRead, byteExpected){
		console.log(' Reading total ' + byteRead+'/'+byteExpected);
	});


	form.parse(req);
/*
	req.on('data',()=>{
		
	});
	
	// 1. stream 생성
	var stream = fs.createReadStream(resourcePath);
		
	// 2. 잘게 쪼개진 stream이 몇번 전송되는지 확인하기 위한 count
	var count = 0;
	
	// 3. 잘게 쪼개진 data를 전송할 수 있으면 data 이벤트 발생
	stream.on('data', function(data){
		count = count + 1;
		console.log('data count='+count);
		// 3.1. data 이벤트가 발생되면 해당 data를 클라이언트로 전송
		res.write(data);
	});
	
	// 4. 데이터 전송이 완료되면 end 이벤트 발생
	stream.on('end', function(){
		console.log('end streaming');
		// 4.1. 클라이언트에 전송완료를 알림
		res.end();
		
	});
	
	// 5. 스트림도중 에러 발생시 error 이벤트 발생
	stream.on('error', function(err){
		console.log(err);
		res.end('500 Internal Server '+err);
	});
	
	req.on('end',()=>{
		
		//console.log("name:"+inputData.name+" , phone:"+inputData.phone);
	});
	*/
});

app.listen(8077,()=>{
	
	console.log('Example app listening on port 8077!');
});

module.exports = app;

/*
var server = http.createServer(function(request, response){
	
	var parsedUrl = url.parse(request.url);
	var resource = parsedUrl.pathname;
	console.log('resource=' + resource);
	
	
	var resourcePath = '.' + resource;
	console.log('resourcePath=' + resourcePath);
	
	// html 페이지 요청이 들어왔을 경우는 텍스트 파일 처리
	if(resource.indexOf('/html/') == 0){
		console.log('html page request');
		fs.readFile(resourcePath, 'utf-8', function(error, data){
			if(error){
				response.writeHead(500, {'Content-Type':'text/html'});
				response.end('500 Internal Server '+ error);
			} else{
				response.writeHead(200, {'Content-Type':'text/html'});
				response.end(data);
			}
		});
	}
	
	
	else if(resource.indexOf('/music/') == 0){
		// 1. stream 생성
		var stream = fs.createReadStream(resourcePath);
		
		// 2. 잘게 쪼개진 stream이 몇번 전송되는지 확인하기 위한 count
		var count = 0;
		
		// 3. 잘게 쪼개진 data를 전송할 수 있으면 data 이벤트 발생
		stream.on('data', function(data){
			count = count + 1;
			console.log('data count='+count);
			// 3.1. data 이벤트가 발생되면 해당 data를 클라이언트로 전송
			response.write(data);
		});
		
		// 4. 데이터 전송이 완료되면 end 이벤트 발생
		stream.on('end', function(){
			console.log('end streaming');
			// 4.1. 클라이언트에 전송완료를 알림
			response.end();
			
		});
		
		// 5. 스트림도중 에러 발생시 error 이벤트 발생
		stream.on('error', function(err){
			console.log(err);
			response.end('500 Internal Server '+err);
		});
		
	}
	
	
	else{
		response.writeHead(404, {'Content-Type':'text/html'});
		response.end('404 Page Not Found');
	}
	
	
});




server.listen(80, function(){
	console.log('Server is running...');
});*/