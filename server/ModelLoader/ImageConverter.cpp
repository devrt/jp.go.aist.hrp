/*!
  @file ImageConverter.cpp
  @brief Implementation of Image Converter class
  @author K.FUKUDA
*/

#include <iostream>
#include "ImageConverter.h"

using namespace std;
using namespace OpenHRP;



//==================================================================================================
/*!
    @brief      initialize "SFImage"

    @note       use before using "SFImage" structure 

    @date       2008-04-14 K.FUKUDA <BR>

    @return     bool true : succeeded / false : failed
*/
//==================================================================================================
bool ImageConverter::initializeSFImage(
    SFImage &   image )
{
    image.width = 0;
    image.height = 0;
    image.numComponents = 0;
    image.pixels.clear();

    return true;
}



//==================================================================================================
/*!
    @brief      convert ImageTexture node to PixelTexture node

    @note       read image data from VrmlImageTexture::url and store pixel data to
                VrmlPixelTexture::image.
                *.png and *.jpg are supported.
                Currentry, multi url is not supported.

    @date       2008-04-14 K.FUKUDA <BR>

    @return     bool true : succeeded / false : failed
*/
//==================================================================================================
bool
ImageConverter::convert(
    VrmlImageTexture &  imageTexture,
    VrmlPixelTexture &  pixelTexture,
	string dirPath )
{
    // ImageTexture�Ɋi�[����Ă��� MFString url �̐����m�F
    if( 0 == imageTexture.url.size() )
    {
        cout << "ImageTexture read error: No urls in ImageTexture node" << "\n";
        return false;
    }
    else if( 1 < imageTexture.url.size() )
    {
        cout << "ImageTexture read warning: multiple texture not supported." << "\n";
        cout << "  loading first url only." << "\n";
    }

    // �g���q���o
    string  fileName = dirPath + *imageTexture.url.begin();
    string  ext = fileName.substr( fileName.rfind( '.' ) );

    // �g���q���������ɕϊ�
    transform( ext.begin(), ext.end(), ext.begin(), (int(*)(int))tolower );

    // SFImage�̍쐬
    if( !ext.compare( ".png" ) )
    {
        loadPNG( fileName, pixelTexture.image );
    }
    else if( !ext.compare( ".jpg" ) )
    {
        loadJPEG( fileName, pixelTexture.image );
    }
    else
    {
       cout << "ImageTexture read error: unsupported format." << '\n';
       return false;
    }
    
    // PixelTexture �̂��̑��̃p�����[�^���R�s�[
    pixelTexture.repeatS = imageTexture.repeatS;
    pixelTexture.repeatT = imageTexture.repeatT;

    return true;
}



//==================================================================================================
/*!
    @brief      load PNG file

    @note       load and fill VrmlPixelTexture::image from PNG.

    @date       2008-04-14 K.FUKUDA <BR>

    @return     bool true : succeeded / false : failed
*/
//==================================================================================================bool
bool
ImageConverter::loadPNG(
    string &    filePath,
    SFImage &   image )
{
    initializeSFImage( image );

    FILE *  fp;

    try
    {
        // �t�@�C���I�[�v��
        fp = fopen( filePath.c_str(), "rb" );
        if( !fp )       throw "File open error.";


        // �V�O�l�`���̊m�F
        png_size_t  number = 8;
        png_byte    header[8];
        int         is_png;

        fread( header, 1, number, fp );
        is_png = !png_sig_cmp( header, 0, number );
        if( !is_png )   throw "File is not png.";


        // png_struct �\���̂̏�����
        png_structp pPng;

        pPng = png_create_read_struct( PNG_LIBPNG_VER_STRING, NULL, NULL, NULL );
        if( !pPng )     throw "Failed to create png_struct";


        // png_info �\���̂̏�����
        png_infop   pInfo;
        
        pInfo = png_create_info_struct( pPng );
        if( !pInfo )
        {
            png_destroy_read_struct( &pPng, NULL, NULL );
            throw "Failed to create png_info";
        }


        // �t�@�C���|�C���^�̃Z�b�g�ƃV�O�l�`���m�F�̂��߃|�C���^���i��ł��邱�Ƃ�ʒm
        png_init_io( pPng, fp );
        png_set_sig_bytes( pPng, (int)number );


        // �������ǂݍ���I/F
        png_read_png( pPng, pInfo, PNG_TRANSFORM_IDENTITY, NULL );


        // �摜�̕��E�����E�`�����l�����擾
        int         numComponents;
        png_uint_32 width   = png_get_image_width( pPng, pInfo );
        png_uint_32 height  = png_get_image_height( pPng, pInfo );
        png_byte    depth   = png_get_bit_depth( pPng, pInfo );

        if( 8 != depth )
            throw "Unsupported color depth.";

        switch( png_get_color_type( pPng, pInfo ) )
        {
        case PNG_COLOR_TYPE_GRAY:
            numComponents = 1;
            break;

        case PNG_COLOR_TYPE_GRAY_ALPHA:
            numComponents = 2;
            break;

        case PNG_COLOR_TYPE_RGB:
            numComponents = 3;
            break;
            
        case PNG_COLOR_TYPE_RGB_ALPHA:
            numComponents = 4;
            break;

        default:
            throw "Unsupported color type.";

        }


        // �摜�f�[�^�ւ̃A�N�Z�X����
        png_bytep   *row_pointers;   // �e�s�̃s�N�Z���E�f�[�^�̃|�C���^�z��
        row_pointers = png_get_rows( pPng, pInfo );


        // SFImage�ɓW�J
        image.width = width;
        image.height = height;
        image.numComponents = numComponents;

        unsigned char   byteData;

        for( png_uint_32 y=0 ; y<height ; y++ )
        {
            for( png_uint_32 x=0 ; x<width*numComponents ; x++ )
            {
                byteData = (unsigned char)row_pointers[y][x];
                image.pixels.push_back( byteData );
            }
        }


        // ���������
        //png_destroy_read_struct( &pPng, &pInfo, &pEndInfo );
        png_destroy_read_struct( &pPng, &pInfo, NULL );

        fclose( fp );
    }

    catch( char * str )
    {
       cout << "PNG read error: " << str << '\n';
       if( fp ) fclose( fp );
       return false;
    }

    return true;
}



//==================================================================================================
/*!
    @brief      load JPEG file

    @note       load and fill VrmlPixelTexture::image from JPEG.

    @date       2008-04-17 K.FUKUDA <BR>

    @return     bool true : succeeded / false : failed
*/
//==================================================================================================bool
bool
ImageConverter::loadJPEG(
    string &    filePath,
    SFImage &   image )
{
    initializeSFImage( image );

    FILE *  fp;

    try
    {
        // File open
        fp = fopen( filePath.c_str(), "rb" );
        if( !fp )       throw "File open error.";


        struct jpeg_decompress_struct   cinfo;
        struct jpeg_error_mgr           jerr;

        JSAMPARRAY  buffer;         // output row buffer
        int         row_stride;     // physical row width in output buffer


        // Step 1: allocate and initialize JPEG decompression object
        cinfo.err = jpeg_std_error( &jerr );
        jpeg_create_decompress( &cinfo );


        // Step 2: specify data source (eg, a file)
        jpeg_stdio_src( &cinfo, fp );

        // Step 3: read file parameters with jpeg_read_header()
        (void)jpeg_read_header( &cinfo, TRUE );


        // Step 4: set parameters for decompression


        // Step 5: Start decompression
        (void)jpeg_start_decompress( &cinfo );


        // get image attribute
        image.width         = cinfo.output_width;
        image.height        = cinfo.output_height;
        image.numComponents = cinfo.out_color_components;
        if( 1 == cinfo.output_components )  throw "Index color is not supported currentry.";


        // JSAMPLEs per row in output buffer
        row_stride = cinfo.output_width * cinfo.output_components;

        // Make a one-row-high sample array that will go away when done with image
        buffer = (*cinfo.mem->alloc_sarray)( (j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1 );

        // Step 6: while (scan lines remain to be read)
        //           jpeg_read_scanlines(...);
        unsigned int    x = 0, y = 0;
        unsigned int    bufferLength = cinfo.output_width * cinfo.out_color_components;
        unsigned char   byteData;

        while( cinfo.output_scanline < cinfo.output_height )
        {
            (void)jpeg_read_scanlines( &cinfo, buffer, 1 );

            for( x=0 ; x<bufferLength ; x++ )
            {
                byteData = (unsigned char)buffer[0][x];
                image.pixels.push_back( byteData );
            }
            y++;
        }

        // Step 7: Finish decompression
        (void)jpeg_finish_decompress( &cinfo );


        // Step 8: Release JPEG decompression object
        jpeg_destroy_decompress( &cinfo );


        fclose( fp );
    }

    catch( char * str )
    {
       cout << "JPEG read error: " << str << '\n';
	   if( fp ) fclose( fp );
       return false;
    }

    return true;
}
