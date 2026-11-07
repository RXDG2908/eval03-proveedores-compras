-- Laboratorio 13: bases de datos de produccion (perfil prod)
-- En dev y test las bases y tablas se crean solas.

CREATE DATABASE IF NOT EXISTS bd_proveedor_prod;
USE bd_proveedor_prod;
CREATE TABLE `proveedor` (
  `id_proveedor` bigint(20) NOT NULL AUTO_INCREMENT,
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `razon_social` varchar(150) NOT NULL,
  `ruc` varchar(11) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id_proveedor`),
  UNIQUE KEY `UKawwnjxxy3vxst1ynt3tlqqbhu` (`ruc`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE DATABASE IF NOT EXISTS bd_producto_prod;
USE bd_producto_prod;
CREATE TABLE `producto` (
  `id_producto` bigint(20) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(200) DEFAULT NULL,
  `id_proveedor` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `precio` double NOT NULL,
  `stock` int(11) NOT NULL,
  PRIMARY KEY (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE DATABASE IF NOT EXISTS bd_compra_prod;
USE bd_compra_prod;
CREATE TABLE `compra` (
  `id_compra` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `id_proveedor` bigint(20) NOT NULL,
  `total` double NOT NULL,
  PRIMARY KEY (`id_compra`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE TABLE `detalle_compra` (
  `id_detalle` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` int(11) NOT NULL,
  `id_producto` bigint(20) NOT NULL,
  `precio` double NOT NULL,
  `subtotal` double NOT NULL,
  `id_compra` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `FK2wtotw2gq3jjuvniri8omlsqe` (`id_compra`),
  CONSTRAINT `FK2wtotw2gq3jjuvniri8omlsqe` FOREIGN KEY (`id_compra`) REFERENCES `compra` (`id_compra`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
